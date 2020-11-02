package phis.application.war.source.temperature.drawshape;

import java.awt.Color;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;

import phis.application.war.source.temperature.ChartProcessor;
import phis.application.war.source.temperature.PTempInfo;
import phis.application.war.source.temperature.TemShapeBean;
import phis.source.utils.BSHISUtil;
import phis.source.utils.IdUtils;

public class LineShape extends AbstractShapeStrategy implements ShapeLineStrategy {
	private List<String> dateList;
	public LineShape(ChartProcessor chart,List<String> dateList){
		this.chart=chart;
		this.dateList=dateList;
	}
	@Override
	public void draw(List<PTempInfo> pTempInfoList) {
		String twLineName=IdUtils.getInstanse().getUID().toString();
		String mbLineName=IdUtils.getInstanse().getUID().toString();
		String xlLineName=IdUtils.getInstanse().getUID().toString();
		List<Map<String,Object>> dxtw=new ArrayList<Map<String,Object>>();//add by lizhi 用于绘制白线实现断线
		
		for(PTempInfo pTempInfo:pTempInfoList){//add by lizhi 拒测、外出体温单线断开，并显示在34℃~35℃之间（按时间排序）
			List<Map<String,Object>> pTempInfoDetailList = pTempInfo.getDetailInfo();
			if(pTempInfoDetailList.size()>0){
				listSort(pTempInfoDetailList);
			}
		}
		
		for(int n=0;n<pTempInfoList.size();n++){
			PTempInfo pTempInfo = pTempInfoList.get(n);
			List<Map<String,Object>> tw=new ArrayList<Map<String,Object>>();
			List<Map<String,Object>> mb=new ArrayList<Map<String,Object>>();
			List<Map<String,Object>> xl=new ArrayList<Map<String,Object>>();
			//设置x轴的偏移方位
			for(int i=0;i<dateList.size();i++){
				String dateStr=dateList.get(i);
				if(dateStr.equals(BSHISUtil.toString(pTempInfo.getInspectionDate()))){
					pTempInfo.setNumDay(i);
				}
			}
			List<Map<String,Object>> pTempInfoDetailList = pTempInfo.getDetailInfo();
			for(int i=0;i<pTempInfoDetailList.size();i++){
				Map<String,Object> itObj = pTempInfoDetailList.get(i);
				if("1".equals(MapUtils.getString(itObj, "XMH"))){
					tw.add(itObj);
				}else if("2".equals(itObj.get("XMH").toString())){
					mb.add(itObj);
				}else if("4".equals(itObj.get("XMH").toString())){
					xl.add(itObj);
				}else if("11".equals(itObj.get("XMH").toString()) && ("拒测".equals(itObj.get("TZNR").toString()) || itObj.get("TZNR").toString().indexOf("外出")>-1)){
					if(i<pTempInfoDetailList.size()-1){
						dxtw.add(pTempInfoDetailList.get(i+1));
					}else if(n<pTempInfoList.size()-1){//隔天取断线点
						boolean flag = false;
						for(int index=1;index<pTempInfoList.size()-n;index++){
							List<Map<String,Object>> dataList = pTempInfoList.get(n+index).getDetailInfo();
							if(dataList.size()>0){
								for(Map<String,Object> data : dataList){
									if("1".equals(MapUtils.getString(data, "XMH"))){
										dxtw.add(data);
										flag = true;
										break;
									}
								}
							}
							if(flag){
								break;
							}
						}
					}
				}
			}
			try {
				//体温线
//				TemShapeBean twbean=new TemShapeBean();
//				twbean.setLineName(twLineName);
//				twbean.setLineColor(Color.BLUE);
//				drawTWLine(tw, pTempInfo.getNumDay(),twbean);
				for(Map<String,Object> twdata:tw){//add by lizhi 拒测、外出体温单线断开，并显示在34℃~35℃之间
					List<Map<String,Object>> dataList = new ArrayList<Map<String,Object>>();
					String cjsjTW = twdata.get("CJSJ")+"";
					boolean flag = false;
					for(Map<String,Object> dxdata:dxtw){
						String cjsjDX = dxdata.get("CJSJ")+"";
						if(cjsjTW.equals(cjsjDX)){
							flag = true;
							break;
						}
					}
					TemShapeBean twbean=new TemShapeBean();
					if(flag){
						twLineName=IdUtils.getInstanse().getUID().toString();
						twbean.setLineColor(Color.WHITE);
						twbean.setLineName(twLineName);
						dataList.add(twdata);
						drawDXLine(dataList, pTempInfo.getNumDay(),twbean);
					}else{
						twbean.setLineColor(Color.BLUE);
						twbean.setLineName(twLineName);
						dataList.add(twdata);
						drawTWLine(dataList, pTempInfo.getNumDay(),twbean);
					}
				}
				//脉搏线
				TemShapeBean mbbean=new TemShapeBean();
				mbbean.setLineName(mbLineName);
				mbbean.setLineColor(Color.RED);
				drawMBLine(mb, pTempInfo.getNumDay(), mbbean);
				
				//脉搏线
				TemShapeBean xlbean=new TemShapeBean();
				xlbean.setLineName(xlLineName);
				xlbean.setLineColor(Color.RED);
				drawMBLine(xl, pTempInfo.getNumDay(), xlbean);
				
				//add by lizhi 2017-11-20画断线
//				TemShapeBean dxbean=new TemShapeBean();
//				dxbean.setLineName(dxLineName);
//				dxbean.setLineColor(Color.BLACK);
//				drawDXLine(dxtw, pTempInfo.getNumDay(), dxbean);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//画脉搏小于心率的线
			for(Map<String,Object> xlObj:xl){
				for(Map<String,Object> mbObj:mb){
					if(MapUtils.getString(xlObj, "HOUR").equals(MapUtils.getString(mbObj, "HOUR"))&&MapUtils.getDouble(xlObj, "TZNR")>MapUtils.getDouble(mbObj, "TZNR")){
						try {
							
							TemShapeBean mbbean=new TemShapeBean();
							mbbean.setLineName(mbLineName);
							mbbean.setLineColor(Color.RED);
							mbbean.setLineName(IdUtils.getInstanse().getUID().toString());
							List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
							list.add(mbObj);
							list.add(xlObj);
							drawMBLine(list, pTempInfo.getNumDay(), mbbean);
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InstantiationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	public static void listSort(List<Map<String, Object>> list){
		Collections.sort(list,new Comparator<Map<String, Object>>() {
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss"); 
				// o1，o2是list中的Map，可以在其内取得值，按其排序，此例为升序，s1和s2是排序字段值
				String s1 = (String) o1.get("CJSJ");
				String s2 = (String) o2.get("CJSJ");
				Date s1Date = null;
				Date s2Date = null;
				try {
					if (s1 != null && !"".equals(s1)) {
						s1Date = formatter.parse(s1);
					}
					if (s2 != null && !"".equals(s2)) {
						s2Date = formatter.parse(s2);
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (s1Date.before(s2Date)) {
					return -1;
				} else {
					return 1;
				}
			}
        });
    }
}
