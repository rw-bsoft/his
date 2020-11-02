package chis.source.phr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.web.context.WebApplicationContext;
import ctd.controller.exception.ControllerException;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.service.core.Service;
import ctd.util.AppContextHolder;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class SubFormLoad implements Service {
	private static final Logger logger = LoggerFactory
			.getLogger(SubFormLoad.class);

	@SuppressWarnings("rawtypes")
	public void execute(Map<String, Object> jsonReq,
			Map<String, Object> jsonRes, Context ctx) throws ValidateException {
		String schemaId = "";
		String pkey = "";
		schemaId = (String) jsonReq.get("schema");
		String empiId = (String) jsonReq.get("empiId");
		SessionFactory sf = (SessionFactory) AppContextHolder.get().getBean(
				"mySessionFactory");
		HibernateTemplate ht = new HibernateTemplate(sf);
		Schema schema = null;
		try {
			schema = SchemaController.instance().get(schemaId);
		} catch (ControllerException e1) {
			e1.printStackTrace();
		}
		String hql = "select " + schema.getKeyItem().getId() + " from "
				+ schemaId + " where empiid = '" + empiId + "'";

		List list = null;
		list = ht.find(hql);

		if (list.size() == 0) {
			Map<String, Object> body = new HashMap<String, Object>();
			jsonRes.put("body", body);
			body.put("empiId", empiId);
			jsonRes.put(Service.RES_CODE, 200);
			jsonRes.put(Service.RES_MESSAGE, "LoadSuccess");
		} else {
			pkey = list.get(0).toString();
			Schema sc = null;
			try {
				sc = SchemaController.instance().get(schemaId);
			} catch (ControllerException e1) {
				e1.printStackTrace();
			}
			if (sc == null) {
				logger.error("schema[" + schemaId + "] not found");
				return;
			}

			SchemaItem key = sc.getKeyItem();
			if (key == null) {
				logger.error("schema[" + schemaId + "] key missing");
				return;
			}

			try {
				Object id = key.toPersistValue(pkey);
				loadRecord(sc, id, jsonRes, ctx);
			} catch (DataAccessException e) {
				logger.error(e.getMessage());
				jsonRes.put(Service.RES_CODE, 501);
				jsonRes.put(Service.RES_MESSAGE,
						"DataAccessException:" + e.getMessage());

			}
		}

	}

	@SuppressWarnings("unchecked")
	private void loadRecord(Schema sc, Object id, Map<String, Object> jsonRes,
			Context ctx) throws DataAccessException {
		SessionFactory sf = (SessionFactory) AppContextHolder.get()
				.getBean("mySessionFactory");
		HibernateTemplate ht = new HibernateTemplate(sf);
		Map<String, Object> rec = null;
		String entryName = sc.getId();
		rec = (Map<String, Object>) ht
				.get(entryName, (java.io.Serializable) id);
		if (rec == null) {
			logger.error("load[" + entryName + "] record for key[" + id
					+ "] not found");
			jsonRes.put(Service.RES_CODE, 504);
			jsonRes.put(Service.RES_MESSAGE, "RecNotFound");
			return;
		}
		List<SchemaItem> items = sc.getItems();
		Map<String, Object> body = new HashMap<String, Object>();
		jsonRes.put("body", body);
		for (SchemaItem si : items) {
			String fid = si.getId();
			Object fv = rec.get(fid);
			if (si.isCodedValue()) {
				Map<String, Object> v = new HashMap<String, Object>();
				v.put("key", fv);
				v.put("text", si.toDisplayValue(fv));
				body.put(fid, v);
			} else {
				body.put(fid, fv);
			}
		}
		body.put(sc.getKeyItem().getId(), id);
		jsonRes.put(Service.RES_CODE, 200);
		jsonRes.put(Service.RES_MESSAGE, "LoadSuccess");
	}
}
