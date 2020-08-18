package com.acuilab.bc.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.netbeans.api.autoupdate.InstallSupport;
import org.netbeans.api.autoupdate.InstallSupport.Validator;
import org.netbeans.api.autoupdate.OperationContainer;
import org.netbeans.api.autoupdate.OperationContainer.OperationInfo;
import org.netbeans.api.autoupdate.OperationException;
import org.netbeans.api.autoupdate.OperationSupport;
import org.netbeans.api.autoupdate.OperationSupport.Restarter;
import org.netbeans.api.autoupdate.UpdateElement;
import org.netbeans.api.autoupdate.UpdateUnit;
import org.netbeans.api.autoupdate.UpdateUnitProvider;
import org.netbeans.api.autoupdate.UpdateUnitProviderFactory;
import org.openide.awt.NotificationDisplayer;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.RequestProcessor;

/**
 * @see 《The Definitive Guide to Netbeans Platform 7》 Chapter 25 Auto Update
 * Services API
 * @author admin
 */
public class AutoInstaller implements Runnable {

    private static final Logger LOG = Logger.getLogger(AutoInstaller.class.getName());
    private static final String UC_NAME = "com_acuilab_bc_main_update_center";

    @Override
    public void run() {
        RequestProcessor.getDefault().post(new AutoInstallerImpl(), 1000);
    }

    private static final class AutoInstallerImpl implements Runnable {

        private final List<UpdateElement> install = new ArrayList<>();
        private final List<UpdateElement> update = new ArrayList<>();
        private boolean isRestartRequested = false;

        @Override
        public void run() {
            System.out.println("WarmUp AutoInstaller ..........................................");
            searchNewAndUpdatedModules();

            OperationContainer<InstallSupport> installContainer = addToContainer(OperationContainer.createForInstall(), install);
            installModules(installContainer);

            OperationContainer<InstallSupport> updateContainer = addToContainer(OperationContainer.createForUpdate(), update);
            installModules(updateContainer);
        }

        public OperationContainer<InstallSupport> addToContainer(OperationContainer<InstallSupport> container, List<UpdateElement> modules) {
            for (UpdateElement e : modules) {
                System.out.println("UpdateElement e=" + e.toString());
                System.out.println("container.canBeAdded(e.getUpdateUnit(), e)=" + container.canBeAdded(e.getUpdateUnit(), e));
                if (container.canBeAdded(e.getUpdateUnit(), e)) {
                    OperationInfo<InstallSupport> operationInfo = container.add(e);
                    System.out.println("operationInfo=" + operationInfo);
                    if (operationInfo != null) {
                        container.add(operationInfo.getRequiredElements());
                    }
                }
            }

            return container;
        }

        public void installModules(OperationContainer<InstallSupport> container) {
            try {
                InstallSupport support = container.getSupport();
                System.out.println("support=" + support);
                if (support != null) {

                    Validator vali = support.doDownload(null, true, true);
                    InstallSupport.Installer inst = support.doValidate(vali, null);
                    Restarter restarter = support.doInstall(inst, null);
                    System.out.println("restarter=" + restarter);
                    if (restarter != null) {
                        support.doRestartLater(restarter);
                        if (!isRestartRequested) {
                            NotificationDisplayer.getDefault().notify(
                                    "该应用程序已更新",
                                    ImageUtilities.loadImageIcon("resource/gourd32.png", false),
                                    "点击此处重新启动",
                                    new RestartAction(support, restarter)
                            );
                            isRestartRequested = true;
                        }
                    }
                }
            } catch (OperationException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        // Searching New and Updated Modules
        public void searchNewAndUpdatedModules() {
            for (UpdateUnitProvider provider : UpdateUnitProviderFactory.getDefault().getUpdateUnitProviders(false)) {
                try {
                    provider.refresh(null, true);
                } catch (IOException ex) {
                    LOG.severe(ex.getMessage());
                }
                for (UpdateUnit u : provider.getUpdateUnits()) {
                    if (!u.getAvailableUpdates().isEmpty()) {
                        if (u.getInstalled() == null) {
                            install.add(u.getAvailableUpdates().get(0));
                        } else {
                            update.add(u.getAvailableUpdates().get(0));
                        }
                    }
                }
            }
            System.out.println("install=" + install.size());
            System.out.println("update=" + update.size());
        }

        // Searching Modules in a Special Update Center
        public void searchNewAndUpdateModulesInDedicatedUC() {
            for (UpdateUnitProvider provider : UpdateUnitProviderFactory.getDefault().getUpdateUnitProviders(false)) {
                try {
                    if (StringUtils.equals(UC_NAME, provider.getName())) {
                        provider.refresh(null, true);

                        for (UpdateUnit u : provider.getUpdateUnits()) {
                            if (!u.getAvailableUpdates().isEmpty()) {
                                if (u.getInstalled() == null) {
                                    install.add(u.getAvailableUpdates().get(0));
                                } else {
                                    update.add(u.getAvailableUpdates().get(0));
                                }
                            }
                        }
                    }
                } catch (IOException ex) {
                    LOG.severe(ex.getMessage());
                }
            }
        }

        private static final class RestartAction implements ActionListener {

            private final InstallSupport support;
            private final OperationSupport.Restarter restarter;

            public RestartAction(InstallSupport support, OperationSupport.Restarter restarter) {
                this.support = support;
                this.restarter = restarter;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    support.doRestart(restarter, null);
                } catch (OperationException ex) {
                    LOG.severe(ex.getMessage());
                }
            }

        }
    }
}
