package com.acuilab.bc.main.wallet;
/** Localizable strings for {@link com.acuilab.bc.main.wallet}. */
class Bundle {
    /**
     * @return <i>我的钱包们</i>
     * @see WalletListTopComponent
     */
    static String CTL_WalletListAction() {
        return org.openide.util.NbBundle.getMessage(Bundle.class, "CTL_WalletListAction");
    }
    /**
     * @return <i>我的钱包们</i>
     * @see WalletListTopComponent
     */
    static String CTL_WalletListTopComponent() {
        return org.openide.util.NbBundle.getMessage(Bundle.class, "CTL_WalletListTopComponent");
    }
    /**
     * @return <i>我的钱包们</i>
     * @see WalletListTopComponent
     */
    static String HINT_WalletListTopComponent() {
        return org.openide.util.NbBundle.getMessage(Bundle.class, "HINT_WalletListTopComponent");
    }
    private Bundle() {}
}
