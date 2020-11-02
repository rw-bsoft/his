<?xml version="1.0" encoding="UTF-8"?>
<application id="chis.application.systemmanage.SYSTEMMANAGE"
	name="系统管理">
	<catagory id="CONF" ref="chis.application.conf.CONF/CONF" />
	<catagory id="DATA" name="基础数据维护">
		<module id="DA01" ref="platform.reg.REG/REGCATA1/REG_01" />
		<module id="DA02" ref="platform.reg.REG/REGCATA1/REG_02" />
		<module id="DA03" ref="platform.reg.REG/REGCATA1/REG_03" />
		<module id="DA04" name="组织架构维护" ref="platform.reg.REG/SYS/SYS_05" />
		<module id="DA05" ref="chis.application.ag.AG/AG/AA1" />
		<module id="DA06" ref="chis.application.pd.PD/PD/S04" />
		<module id="DA07" ref="chis.application.pd.PD/PD/S04-1" type="1" />
		<module id="DA08" ref="chis.application.pd.PD/PD/S04-2" type="1" />
		<module id="DA09" ref="chis.application.pd.PD/PD/S04-3" type="1" />
		<module id="DA10" ref="chis.application.pd.PD/PD/S04-4" type="1" />
		<module id="DA11" ref="chis.application.pd.PD/PD/S04-5" type="1" />
		<!-- <module id="DA12" ref="chis.application.pd.PD/PD/S04-6" type="1"/> -->
		<module id="DA13" ref="chis.application.pd.PD/PD/S04-7" type="1" />
		<module id="DA14" ref="chis.application.pd.PD/PD/S04-8" type="1" />
		<module id="DA15" ref="chis.application.pif.PIF/PIF/S03" />
		<module id="DA16" ref="chis.application.pif.PIF/PIF/S03_1"
			type="1" />
		<module id="DA17" ref="chis.application.pub.PUB/DRUG/DRUG01" />
		<module id="DA18" ref="chis.application.pub.PUB/PHT/PHT01" />
		<module id="DA19" ref="chis.application.config.CONFIG/CONFIG/CONFIG01" />
		<module id="DA20" ref="chis.application.quality.QUALITY/QUALITY/QUALITY01" />
	</catagory> 
	<catagory id="SYS" name="系统维护">
		<module id="SYS01" ref="platform.reg.REG/SYS/SYS_01" />
		<module id="SYS02" ref="platform.reg.REG/SYS/SYS_03" />
		<module id="SYS03" ref="platform.reg.REG/SYS/SYS_02" />
		<module id="SYS04" ref="platform.reg.REG/SYS/SYS_04" />
		<module id="SYS05" ref="platform.reg.REG/SYS/SYS_07" />
		<module id="SYS06" ref="platform.reg.REG/SYS/SYS_08" />
	</catagory>
	<catagory id="PC" ref="chis.application.pc.PC/PC" />
	<catagory id="TR" name="肿瘤早发现">
		<module id="TR01" ref="chis.application.tr.TR/TR/TR01"/>
		<module id="TR01_01" ref="chis.application.tr.TR/TR/TR01_01" type="1"/>
		<module id="TR01_0101" ref="chis.application.tr.TR/TR/TR01_0101" type="1"/>
		<module id="TR01_0102" ref="chis.application.tr.TR/TR/TR01_0102" type="1"/>
		<module id="TR01_0102_01" ref="chis.application.tr.TR/TR/TR01_0102_01" type="1"/>
		<module id="TR01_0102_02" ref="chis.application.tr.TR/TR/TR01_0102_02" type="1"/>
		<module id="TR01_0102_03" ref="chis.application.tr.TR/TR/TR01_0102_03" type="1"/>
		<module id="TR02" ref="chis.application.tr.TR/TR/TR02"/>
		<module id="TR03" ref="chis.application.tr.TR/TR/TR03"/>
		<module id="TR03_01" ref="chis.application.tr.TR/TR/TR03_01" type="1"/>
		<module id="TR03_0101" ref="chis.application.tr.TR/TR/TR03_0101" type="1"/>
		<module id="TR03_0102" ref="chis.application.tr.TR/TR/TR03_0102" type="1"/>
		<module id="TR03_0103" ref="chis.application.tr.TR/TR/TR03_0103" type="1"/>
	</catagory>
	<catagory id="MM" name="动态模板维护">
		<module id="MM01" ref="chis.application.mpm.MPM/MPM/MPM1" />
		<module id="MM02" ref="chis.application.mpm.MPM/MPM/MPM1_1"
			type="1" />
		<module id="MM03" ref="chis.application.mpm.MPM/MPM/MPM1_2"
			type="1" />
		<module id="MM04" ref="chis.application.mpm.MPM/MPM/MPM1_3"
			type="1" />
		<module id="MM05" ref="chis.application.mpm.MPM/MPM/MPM1_4"
			type="1" />
		<module id="MM06" ref="chis.application.mpm.MPM/MPM/MPM2" />
		<module id="MM07" ref="chis.application.mpm.MPM/MPM/MPM2_1"
			type="1" />
		<module id="MM08" ref="chis.application.mpm.MPM/MPM/MPM2_2"
			type="1" />
		<module id="MM09" ref="chis.application.mpm.MPM/MPM/MPM2_3"
			type="1" />
		<module id="MM10" ref="chis.application.mpm.MPM/MPM/MPM2_4"
			type="1" />
		<module id="MM11" ref="chis.application.mpm.MPM/MPM/MPM2_5"
			type="1" />
		<module id="MM12" ref="chis.application.mpm.MPM/MPM/MPM2_6"
			type="1" />
		<module id="MM13" ref="chis.application.mpm.MPM/MPM/MPM2_7"
			type="1" />
		<module id="MM14" ref="chis.application.mpm.MPM/MPM/MPM2_8"
			type="1" />
		<module id="MM15" ref="chis.application.pub.PUB/DV/DV01" />
		<module id="MM16" ref="chis.application.pub.PUB/DV/DV02" type="1" />
		<module id="MM17" ref="chis.application.pub.PUB/DV/DV03"/>
		<module id="MM18" ref="chis.application.mpm.MPM/MPM/MPM3"/>
	</catagory>
	<catagory id="REL" name="添加关联管理">
		<module id="REL01" ref="chis.application.rel.REL/REL/REL01" />
		<module id="REL02" ref="chis.application.rel.REL/REL/REL02" />
		<module id="REL03" ref="chis.application.rel.REL/REL/REL03" />
		<module id="REL0301" ref="chis.application.rel.REL/REL/REL0301" />
	</catagory>
	<catagory id="SCC" ref="chis.application.scm.SCM/SCC" />
</application>

