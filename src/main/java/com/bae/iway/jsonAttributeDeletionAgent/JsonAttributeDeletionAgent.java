package com.bae.iway.jsonAttributeDeletionAgent;

import java.util.Iterator;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ibi.annotations.ISMAgent;
import com.ibi.annotations.ISMProperty;
import com.ibi.edaqm.XDAgent;
import com.ibi.edaqm.XDDocument;
import com.ibi.edaqm.XDException;

@ISMAgent(label = "Json Attribute Deletion Agent", description = "agent description goes here", events = {
		"EX_FAIL_FORMAT | Failed Format | Not Correct Format" })
public class JsonAttributeDeletionAgent extends XDAgent {

	@ISMProperty(label = "Attribute", description = "Json Atrribute to be removed irrelevant of where it exists in the json structure. All instances will be deleted", group = "Main", required = true)
	private String Attribute;

	@Override
	public String execute(XDDocument docIn, XDDocument docOut) throws XDException {

		JsonNode rootNode = docIn.getJson();
		if(rootNode == null) {
			docIn.moveTo(docOut);
			return EX_FAIL_FORMAT;
		}
        removeKeepAttributes(rootNode);
		docOut.setJson(rootNode);
		return EX_SUCCESS;
	}
    private void removeKeepAttributes(JsonNode node) {
        if (node.isObject()) {
            ObjectNode objectNode = (ObjectNode) node;
            Iterator<Map.Entry<String, JsonNode>> fields = objectNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                if (entry.getKey().equals(Attribute)) {
                    fields.remove();
                } else {
                    removeKeepAttributes(entry.getValue());
                }
            }
        } else if (node.isArray()) {
            for (JsonNode item : node) {
                removeKeepAttributes(item);
            }
        }
    }

}
