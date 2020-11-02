package phis.application.reg.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.application.pix.source.EmpiModel;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.AbstractActionService;
import ctd.util.context.Context;

public class HealthCardModel extends AbstractActionService{
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(HealthCardModel.class);

	public HealthCardModel(BaseDAO dao) {
		this.dao = dao;
	}
	
	/**
	 * 卡号查询
	 * 
	 * @param CardOrMZHM
	 * @param res
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> doCheckHasCardInfo(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> body=(Map<String,Object>)req.get("body");
		Boolean flag=(Boolean)req.get("flag");
		Map<String, Object> result = new HashMap<String, Object>();
		res.put("hasCardNo", false);
		if (body.get("cards") != null) {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			list = (List<Map<String, Object>>) body.get("cards");
			Map<String, Object> card = list.get(0);
			Map<String, Object> mzxh = list.get(1);
			String cardNo = card.get("cardNo").toString();
			String mzxhStr = mzxh.get("cardNo").toString();
			if(mzxhStr==null || "".equals(mzxhStr)){
				return res;
			}
			if(!cardNo.equals(mzxhStr) && !flag){
				String cardsql="select empiId as empiId,cardNo as cardNo from MPI_Card where cardNo=:cardNo and cardTypeCode=:cardTypeCode";
				Map<String, Object> pa=new HashMap<String, Object>();
				pa.put("cardNo", cardNo);
				pa.put("cardTypeCode", "01");
				try {
					Map<String, Object> cardno=dao.doLoad(cardsql, pa);
					if(cardno!=null  && cardno.size() > 0){
						res.put("empiId", cardno.get("empiId")+"");
						res.put("hasCardNo", true);
					}
				} catch (PersistentDataOperationException e1) {
					e1.printStackTrace();
				}
				res.put("body", result);
			}else{
				String cardsql="select empiId as empiId from MPI_Card where cardNo=:cardNo and cardTypeCode=:cardTypeCode";
				Map<String, Object> pa=new HashMap<String, Object>();
				pa.put("cardNo", cardNo);
				pa.put("cardTypeCode", "04");
				try {
					Map<String, Object> empiId=dao.doLoad(cardsql, pa);
					if(empiId!=null  && empiId.size() > 0){
						String empiIdStr = empiId.get("empiId")+"";
						if(empiIdStr==null || "".equals(empiIdStr)){
							return res;
						}
						EmpiModel empiModel = new EmpiModel(dao);
						empiModel.deleteCardByEmpiId(empiIdStr);
						res.put("empiId", empiIdStr);
						res.put("cardNo", cardNo);
						String mzxhsql="select MZHM as MZHM from MS_BRDA where EMPIID=:EMPIID ";
						pa.clear();
						pa.put("EMPIID", empiIdStr);
						List<Map<String, Object>> mzhms = dao.doQuery(mzxhsql, pa);
						if(mzhms.size()>0){
							mzxhStr = mzhms.get(0).get("MZHM")+"";
							res.put("mzxh", mzxhStr);
							//保存医保卡
							Map<String, Object> card1 = new HashMap<String, Object>();
							card1.put("empiId", empiIdStr);
							card1.put("cardNo", cardNo);
							card1.put("cardTypeCode", "01");
							card1.put("status", "0");
							empiModel.saveCard(card1);
							//保存就诊卡
							Map<String, Object> card2 = new HashMap<String, Object>();
							card2.put("empiId", empiIdStr);
							card2.put("cardNo", mzxhStr);
							card2.put("cardTypeCode", "04");
							card2.put("status", "0");
							empiModel.saveCard(card2);
						}
						res.put("hasCardNo", true);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
		return res;
	}
}
