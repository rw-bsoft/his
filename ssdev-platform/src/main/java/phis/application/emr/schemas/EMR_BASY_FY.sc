<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="EMR_BASY_FY" alias="住院病案首页-续（费用）">
	<item id="JLXH" alias="记录序号" type="long" length="18" not-null="1" generator="assigned" pkey="true"/>
	<item id="JZXH" alias="就诊序号" type="long" length="18"/>
	<item id="BRID" alias="病人ID" type="long" length="18"/>
	<item id="JGID" alias="机构ID" type="string" length="20"/>
	<item id="ZYZFY" alias="住院总费用（元）" type="double" length="10" precision="2" layout="FYXX"/>
	<item id="ZFJE" alias="住院总费用-自付金额（元）" type="double" length="10" precision="2" layout="FYXX"/>
	<item id="YBYLFWF" alias="综合医疗服务费-一般医疗服务费" type="double" length="10" precision="2" layout="FYXX"/>
	<item id="YBZLCZF" alias="综合医疗服务费-一般治疗操作费" type="double" length="10" precision="2" layout="FYXX"/>
	<item id="HLF" alias="综合医疗服务费-护理费" type="double" length="10" precision="2" layout="FYXX"/>
	<item id="QTFY" alias="综合医疗服务费-其他费用" type="double" length="10" precision="2" layout="FYXX"/>
	<item id="BLZDF" alias="诊断-病理诊断费" type="double" length="10" precision="2" layout="FYXX"/>
	<item id="SYSZDF" alias="诊断-实验室诊断费" type="double" length="10" precision="2" layout="FYXX"/>
	<item id="YXXZDF" alias="诊断-影像学诊断费" type="double" length="10" precision="2" layout="FYXX"/>
	<item id="LCZDXMF" alias="诊断-临床诊断项目费" type="double" length="10" precision="2" layout="FYXX"/>
	<item id="FSSZLXMF" alias="治疗-非手术治疗项目费" type="double" length="10" precision="2" layout="FYXX"/>
	<item id="LCWLZLF" alias="治疗-非手术治疗项目费-临床物理治疗费" type="double" length="10" precision="2" layout="FYXX"/>
	<item id="SSZLF" alias="治疗-手术治疗费" type="double" length="10" precision="2" layout="FYXX"/>
	<item id="MZF" alias="治疗-手术治疗费-麻醉费" type="double" length="10" precision="2" layout="FYXX"/>
	<item id="SSF" alias="治疗-手术治疗费-手术费" type="double" length="10" precision="2" layout="FYXX"/>
	<item id="KFF" alias="康复费" type="double" length="10" precision="2" layout="FYXX"/>
	<item id="ZYZLF" alias="中医治疗费" type="double" length="10" precision="2" layout="FYXX"/>
	<item id="XYF" alias="西药费" type="double" length="10" precision="2" layout="FYXX"/>
	<item id="KJYWFY" alias="西药费-抗菌药物费用" type="double" length="10" precision="2" layout="FYXX"/>
	<item id="ZCYF" alias="中药费-中成药费" type="double" length="10" precision="2" layout="FYXX"/>
	<item id="ZCY" alias="中药费-中草药费" type="double" length="10" precision="2" layout="FYXX"/>
	<item id="XF" alias="血费" type="double" length="10" precision="2" layout="FYXX"/>
	<item id="BDBLZPF" alias="白蛋白类制品费" type="double" length="10" precision="2" layout="FYXX"/>
	<item id="QDBLZPF" alias="球蛋白类制品费" type="double" length="10" precision="2" layout="FYXX"/>
	<item id="NXYZLZPF" alias="凝血因子类制品费" type="double" length="10" precision="2" layout="FYXX"/>
	<item id="XBYZLZPF" alias="细胞因子类制品费" type="double" length="10" precision="2" layout="FYXX"/>
	<item id="JCYCLF" alias="检查用一次性医用材料费" type="double" length="10" precision="2" layout="FYXX"/>
	<item id="ZLYCLF" alias="治疗用一次性医用材料费" type="double" length="10" precision="2" layout="FYXX"/>
	<item id="SSYCLF" alias="手术用一次性医用材料费" type="double" length="10" precision="2" layout="FYXX"/>
	<item id="QTF" alias="其他费" type="double" length="10" precision="2" layout="FYXX"/>
</entry>
