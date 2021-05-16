package com.acuilab.bc.cfx.solidity;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;
import org.web3j.abi.datatypes.Address;
import org.web3j.codegen.SolidityFunctionWrapper;
import org.web3j.protocol.ObjectMapperFactory;
import org.web3j.protocol.core.methods.response.AbiDefinition;

/**
 *
 * @author acuilab.com
 */
public class JavaClassGenerator {
    private static final Logger LOG = Logger.getLogger(JavaClassGenerator.class.getName());
    
    private static final String DEFAULT_INCLUDE = "**/*.sol";
    
    protected String packageName;

    // relative or absolute path of the generated files (java, bin, abi)
    // src/main/java
    protected String sourceDestination;

    protected FileSet soliditySourceFiles = new FileSet();
    
    // Filter (<include> or <exclude>) contracts based on the name.
    /**
     * <contract>
     *   <includes>
     *    <include>greeter</include>
     *   </includes>
     *   <excludes>
     *    <exclude>mortal</exclude>
     *   <excludes>
     * </contracts>
     */
    protected Contract contract;
    
    // A list (<pathPrefixe>) of replacements of dependency replacements inside Solidity contract.
    protected String[] pathPrefixes = new String[0];
    
    // generate Java Classes(java), ABI(abi) and/or BIN (bin) Files (comma separated)
    protected String outputFormat;
    
    // Creates Java Native Types (instead of Solidity Types)
    protected boolean nativeJavaType = true;
    
    public JavaClassGenerator(String rootDirectory, boolean nativeJavaType, String outputFormat, String sourceDestination, String packageName) {
        this.nativeJavaType = nativeJavaType;
        this.outputFormat = outputFormat;
        this.packageName = packageName;
        this.sourceDestination = sourceDestination;
        
        soliditySourceFiles.setDirectory(rootDirectory);
        soliditySourceFiles.setIncludes(Collections.singletonList(DEFAULT_INCLUDE));
    }

    public void execute() throws JavaClassGeneratorException {
        String[] files = new FileSetManager().getIncludedFiles(soliditySourceFiles);
        if (files != null) {
            processContractFile(Stream.of(files)
                    .filter(f -> {
                        LOG.log(Level.INFO, "Adding to process ''{0}''", f);
                        return true;
                    })
                    .collect(Collectors.toList()));
        }
    }
    
    private void processContractFile(Collection<String> files) throws JavaClassGeneratorException {
        String result = parseSoliditySources(files);
        processResult(result, "\tNo Contract found in files '" + files + "'");
    }
    
    private String parseSoliditySources(Collection<String> includedFiles) throws JavaClassGeneratorException {
        if (includedFiles.isEmpty()) {
            return "{}";
        }
        CompilerResult result = SolidityCompiler.getInstance().compileSrc(
                soliditySourceFiles.getDirectory(),
                includedFiles,
                pathPrefixes,
                SolidityCompiler.Options.ABI,
                SolidityCompiler.Options.BIN,
                SolidityCompiler.Options.INTERFACE,
                SolidityCompiler.Options.METADATA
        );
        if (result.isFailed()) {
            throw new JavaClassGeneratorException("Could not compile Solidity files\n" + result.errors);
        }

        LOG.log(Level.INFO, "\t\tResult:\t{0}", result.output);
        if (result.errors.contains("Warning:")) {
            LOG.log(Level.INFO, "\tCompile Warning:\n{0}", result.errors);
        } else {
            LOG.log(Level.INFO, "\t\tError: \t{0}", result.errors);
        }
        return result.output;
    }
    
    private void processResult(String result, String warnMsg) throws JavaClassGeneratorException {
        Map<String, Map<String, String>> contracts = extractContracts(result);
        if (contracts == null) {
            LOG.severe(warnMsg);
            return;
        }
        for (Map.Entry<String, Map<String, String>> entry : contracts.entrySet()) {
            String contractName = entry.getKey();
            if (isFiltered(contractName)) {
                LOG.log(Level.INFO, "\tContract ''{0}'' is filtered", contractName);
                continue;
            }
            try {
                Map<String, String> contractResult = entry.getValue();
                generatedJavaClass(contractResult, contractName);
                generatedAbi(contractResult, contractName);
                generatedBin(contractResult, contractName);
                LOG.log(Level.INFO, "\tBuilt Class for contract ''{0}''", contractName);
            } catch (ClassNotFoundException | IOException ioException) {
                LOG.log(Level.SEVERE, "Could not build java class for contract '" + contractName + "'", ioException);
            }
        }
    }
    
    private boolean isFiltered(String contractName) {
        if (contract == null) {
            return false;
        }

        if (contract.getExcludes() != null && !contract.getExcludes().isEmpty()) {
            return contract.getExcludes().contains(contractName);
        }

        if (contract.getIncludes() == null || contract.getIncludes().isEmpty()) {
            return false;
        } else {
            return !contract.getIncludes().contains(contractName);
        }
    }

    private Map<String, Map<String, String>> extractContracts(String result) throws JavaClassGeneratorException {
        JsonParser jsonParser = new JsonParser();
        Map<String, Object> json = jsonParser.parseJson(result);
        Map<String, Map<String, String>> contracts = (Map<String, Map<String, String>>) json.get("contracts");
        if (contracts == null) {
            LOG.severe("no contracts found");
            return null;
        }
        Map<String, String> contractRemap = new HashMap<>();

        HashSet<String> contractsKeys = new HashSet<>(contracts.keySet());
        for (String contractFilename : contractsKeys) {
            Map<String, String> contractMetadata = contracts.get(contractFilename);
            String metadata = contractMetadata.get("metadata");
            if (metadata == null || metadata.length() == 0) {
                contracts.remove(contractFilename);
                continue;
            }
            LOG.log(Level.INFO, "metadata:{0}", metadata);
            Map<String, Object> metadataJson = jsonParser.parseJson(metadata);
            Object settingsMap = metadataJson.get("settings");
            // FIXME this generates java files for interfaces with >org.ethereum:solcJ-all:0.5.2 , because the compiler generates now metadata.
            if (settingsMap != null) {
                Map<String, String> compilationTarget = ((Map<String, Map<String, String>>) settingsMap).get("compilationTarget");
                if (compilationTarget != null) {
                    for (Map.Entry<String, String> entry : compilationTarget.entrySet()) {
                        String value = entry.getValue();
                        contractRemap.put(contractFilename, value);
                    }
                }
            }
            Map<String, String> compiledContract = contracts.remove(contractFilename);
            String contractName = contractRemap.get(contractFilename);
            contracts.put(contractName, compiledContract);
        }
        return contracts;
    }
    
    private void generatedJavaClass(Map<String, String> results, String contractName) throws IOException, ClassNotFoundException {
        if (!StringUtils.containsIgnoreCase(outputFormat, "java")) {
            return;
        }

        int addressLength = Address.DEFAULT_LENGTH / Byte.SIZE;
        boolean primitiveTypes = false;

        List<AbiDefinition> functionDefinitions = loadContractDefinition(results.get(SolidityCompiler.Options.ABI.getName()));


        if (functionDefinitions.isEmpty()) {
            LOG.severe("Unable to parse input ABI file");
            return;
        }

        new SolidityFunctionWrapper(
                nativeJavaType,
                primitiveTypes,
                false, //generateSendTxForCalls
                addressLength)
                .generateJavaFiles(
                        org.web3j.tx.Contract.class,
                        contractName,
                        results.get(SolidityCompiler.Options.BIN.getName()),
                        functionDefinitions,
                        sourceDestination,
                        packageName,
                        null

                );
    }
    
    private void generatedAbi(Map<String, String> contractResult, String contractName) {
        if (!StringUtils.containsIgnoreCase(outputFormat, "abi")) {
            return;
        }

        String abiJson = contractResult.get(SolidityCompiler.Options.ABI.getName());
        try {
            String filename = contractName + ".json";
            Path path = createPath(sourceDestination);
            Files.write(Paths.get(path.toString(), filename), abiJson.getBytes());
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Could not build abi file for contract '" + contractName + "'", e);
        }
    }

    private void generatedBin(Map<String, String> contractResult, String contractName) {
        if (!StringUtils.containsIgnoreCase(outputFormat, "bin")) {
            return;
        }

        String binJson = contractResult.get(SolidityCompiler.Options.BIN.getName());
        try {
            String filename = contractName + ".bin";
            Path path = createPath(sourceDestination);

            Files.write(Paths.get(path.toString(), filename), binJson.getBytes());
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Could not build bin file for contract '" + contractName + "'", e);
        }
    }
    
    protected List<AbiDefinition> loadContractDefinition(String absFile) throws IOException {
        ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
        AbiDefinition[] abiDefinition = objectMapper.readValue(absFile, AbiDefinition[].class);
        return Arrays.asList(abiDefinition);
    }
    
    private Path createPath(String destinationPath) throws IOException {
        Path path = Paths.get(destinationPath, packageName);

        if (!path.toFile().exists()) {
            Files.createDirectories(path);
        }
        return path;
    }
}
