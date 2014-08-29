package at.gv.egiz.param_tests.provider;

import java.util.Map;

/**
 * Connector data class which specifies which
 * connector/IPlainSigner-implementation to use.
 * 
 * @author mtappler
 *
 */
public class ConnectorData {
    /**
     * the type of the connector, named the same as in the CLI
     */
    private String connectorType;
    /**
     * additional parameters like key alias if a keystore is used
     */
    private Map<String, String> connectorParameters;

    /**
     * Constructor initializing both attributes.
     * 
     * @param connectorType
     *            type of the connector
     * @param connectorParameters
     *            additional parameters
     */
    public ConnectorData(String connectorType,
            Map<String, String> connectorParameters) {
        super();
        this.connectorType = connectorType;
        this.connectorParameters = connectorParameters;
    }

    /**
     * getter
     * 
     * @return type of the connector to use
     */
    public String getConnectorType() {
        return connectorType;
    }

    /**
     * getter
     * 
     * @return additional parameters for the connector
     */
    public Map<String, String> getConnectorParameters() {
        return connectorParameters;
    }
}
