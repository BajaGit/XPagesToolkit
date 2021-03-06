package org.openntf.xpt.agents.servlet.commands;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import org.openntf.xpt.agents.beans.XPTAgentBean;
import org.openntf.xpt.agents.servlet.IXPTServletCommand;
import org.openntf.xpt.core.dss.changeLog.ChangeLogService;
import org.openntf.xpt.core.dss.encryption.EncryptionService;
import org.openntf.xpt.core.utils.HttpResponseSupport;

import com.ibm.domino.services.util.JsonWriter;

public class CheckAgents implements IXPTServletCommand {

	@Override
	public void processActionH(HttpServletResponse resp, XPTAgentBean agentBean, FacesContext context) {
		try {

			// CHECK if a possible Encryption Provider is loaded
			EncryptionService.getInstance().agentLoadProvider();
			// CHECK if a possible ChangLogProvider is loaded
			ChangeLogService.getInstance().getChangeLogProcessors();

			HttpResponseSupport.setJSONUTF8ContentType(resp);
			JsonWriter jsWriter = new JsonWriter(resp.getWriter(), true);
			jsWriter.startObject();
			jsWriter.startProperty("status");
			jsWriter.outStringLiteral("ok");
			jsWriter.endProperty();

			jsWriter.startProperty("checkagents");
			jsWriter.outIntLiteral(agentBean.checkSchedule(context));
			jsWriter.endProperty();
			jsWriter.endObject();

			jsWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
