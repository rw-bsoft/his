<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.diseasemanage.DISEASEMANAGE" name="疾病管理">
	<!-- 将老年人管理挪到“健康管理”模块下
	<catagory id="OHR" ref="chis.application.ohr.OHR/OHR" />-->
	<catagory id="RVC" ref="chis.application.rvc.RVC/RVC" />
	<catagory id="HY" ref="chis.application.hy.HY/HY" />
	<catagory id="HC" ref="chis.application.hc.HC/HC" />
	<catagory id="DBS" ref="chis.application.dbs.DBS/DBS" />
	<catagory id="PSY" ref="chis.application.psy.PSY/PSY" />
	<catagory id="TR" name="肿瘤管理">
		<module id="TR01_02" ref="chis.application.tr.TR/TR/TR01_02"/>
		<module id="TR01_0201" ref="chis.application.tr.TR/TR/TR01_0201" type="1"/>
		<module id="TR01_0202" ref="chis.application.tr.TR/TR/TR01_0202" type="1"/>
		<module id="TR01_0202_01" ref="chis.application.tr.TR/TR/TR01_0202_01" type="1"/>
		<module id="TR01_0202_02" ref="chis.application.tr.TR/TR/TR01_0202_02" type="1"/>
		<module id="TR01_0202_03" ref="chis.application.tr.TR/TR/TR01_0202_03" type="1"/>
		<module id="TR01_03" ref="chis.application.tr.TR/TR/TR01_03"/>
		<module id="PMH_THQ" ref="chis.application.tr.TR/TR/PMH_THQ" type="1"/>
		<module id="THQ" ref="chis.application.tr.TR/TR/THQ" type="1"/>
		<module id="HQGCForm" ref="chis.application.tr.TR/TR/HQGCForm" type="1"/>
		<module id="HQForm" ref="chis.application.tr.TR/TR/HQForm" type="1"/>
		<module id="THQM" ref="chis.application.tr.TR/TR/THQM" type="1"/>
		<module id="THQM_GCForm" ref="chis.application.tr.TR/TR/THQM_GCForm" type="1"/>
		<module id="THQM_TQForm" ref="chis.application.tr.TR/TR/THQM_TQForm" type="1"/>
		<module id="TR04" ref="chis.application.tr.TR/TR/TR04"/>
		<module id="TR04_01" ref="chis.application.tr.TR/TR/TR04_01" type="1"/>
		<module id="TR04_02" ref="chis.application.tr.TR/TR/TR04_02" type="1"/>
		<module id="TR05" ref="chis.application.tr.TR/TR/TR05"/>
		<module id="TR05_01" ref="chis.application.tr.TR/TR/TR05_01" type="1"/>
		<module id="TR05_0101" ref="chis.application.tr.TR/TR/TR05_0101" type="1"/>
		<module id="TR05_0102" ref="chis.application.tr.TR/TR/TR05_0102" type="1"/>
		<module id="TR05_0103" ref="chis.application.tr.TR/TR/TR05_0103" type="1"/>
		<module id="TR06" ref="chis.application.tr.TR/TR/TR06"/>
		<module id="TR0601" ref="chis.application.tr.TR/TR/TR0601" type="1"/>
		<module id="TR0601_01" ref="chis.application.tr.TR/TR/TR0601_01" type="1"/>
		<module id="TR0601_0101" ref="chis.application.tr.TR/TR/TR0601_0101" type="1"/>
		<module id="TR0601_0101_01" ref="chis.application.tr.TR/TR/TR0601_0101_01" type="1"/>
		<module id="TR0601_0101_02" ref="chis.application.tr.TR/TR/TR0601_0101_02" type="1"/>
		<module id="TR0601_0102" ref="chis.application.tr.TR/TR/TR0601_0102" type="1"/>
		<module id="TR0601_0103" ref="chis.application.tr.TR/TR/TR0601_0103" type="1"/>
		<module id="TR0601_0103_01" ref="chis.application.tr.TR/TR/TR0601_0103_01" type="1"/>
		<module id="TR0601_0103_02" ref="chis.application.tr.TR/TR/TR0601_0103_02" type="1"/>
		<module id="TR0602" ref="chis.application.tr.TR/TR/TR0602"/>
		<module id="TR0602_01" ref="chis.application.tr.TR/TR/TR0602_01"/>
		<module id="TR0702" ref="chis.application.tr.TR/TR/TR0702"/>
		<module id="TR0702_01" ref="chis.application.tr.TR/TR/TR0702_01" type="1"/>
		<module id="TR07" ref="chis.application.tr.TR/TR/TR07"/>
		<module id="TR0701" ref="chis.application.tr.TR/TR/TR0701" type="1"/>
		<module id="TR0701_M" ref="chis.application.tr.TR/TR/TR0701_M" type="1"/>
		<module id="TR0701_01" ref="chis.application.tr.TR/TR/TR0701_01" type="1"/>
		<module id="TR0701_02" ref="chis.application.tr.TR/TR/TR0701_02" type="1"/>
		<module id="TR0701_03" ref="chis.application.tr.TR/TR/TR0701_03" type="1"/>
		<module id="TR08" ref="chis.application.tr.TR/TR/TR08"/>
		<module id="TR0801" ref="chis.application.tr.TR/TR/TR0801" type="1"/>
		<module id="TR0801_01" ref="chis.application.tr.TR/TR/TR0801_01" type="1"/>
		<module id="TR0801_0101" ref="chis.application.tr.TR/TR/TR0801_0101" type="1"/>
		<module id="TR0801_0102" ref="chis.application.tr.TR/TR/TR0801_0102" type="1"/>
		<module id="TR0801_0103" ref="chis.application.tr.TR/TR/TR0801_0103" type="1"/>
		<module id="TR0801_02" ref="chis.application.tr.TR/TR/TR0801_02" type="1"/>
		<module id="TR0801_0201" ref="chis.application.tr.TR/TR/TR0801_0201" type="1"/>
		<module id="TR0801_0202" ref="chis.application.tr.TR/TR/TR0801_0202" type="1"/>
		<module id="TR_PMHView" ref="chis.application.tr.TR/TR/TR_PMHView" type="1"/>
		<module id="TR_THQM" ref="chis.application.tr.TR/TR/TR_THQM" type="1"/>
		<module id="TR_TCRList" ref="chis.application.tr.TR/TR/TR_TCRList" type="1"/>
		<module id="TR_PMH" ref="chis.application.tr.TR/TR/TR_PMH" type="1"/>
		<module id="TR_PMH_List" ref="chis.application.tr.TR/TR/TR_PMH_List" type="1"/>
		<module id="TR_PMH_Form" ref="chis.application.tr.TR/TR/TR_PMH_Form" type="1"/>
		<module id="THQList" ref="chis.application.tr.TR/TR/THQList" type="1"/>
		<module id="TR0801_0101_2" ref="chis.application.tr.TR/TR/TR0801_0101_2" type="1"/>
	</catagory>
	<catagory id="DEF" ref="chis.application.def.DEF/DEF" />
	<catagory id="CVD" ref="chis.application.cvd.CVD/CVD" />
	<catagory id="INC" ref="chis.application.inc.INC/INC" />
	<catagory id="CONS" ref="chis.application.cons.CONS/CONS" />
	<catagory id="DR" ref="chis.application.dr.DR/DR" />
	<catagory id="MZF" ref="chis.application.mzf.MZF/MZF" />
</application>