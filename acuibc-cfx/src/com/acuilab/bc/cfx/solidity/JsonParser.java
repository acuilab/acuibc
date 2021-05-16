package com.acuilab.bc.cfx.solidity;

import java.util.Map;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author acuilab.com
 */
public class JsonParser {

    JSONParser jsonParser;


    public JsonParser() {
        jsonParser = new JSONParser();
    }

    public Map<String, Object> parseJson(String jsonString) throws JavaClassGeneratorException {

        try {
            return (Map<String, Object>) jsonParser.parse(jsonString);
        } catch (ParseException e) {
            throw new JavaClassGeneratorException("Could not parse SolC result", e);
        }
    }
}
