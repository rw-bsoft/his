<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.hr.schemas.EHR_JBXX2011" alias="基本信息(2011版)" sort="a.phrId desc" version="1332744636000" filename="E:\MyProject\BSCHISWorkspace\BSCHIS\WebRoot\WEB-INF\config\schema\ehr/EHR_HealthRecord.xml">
	<item id="personName" alias="姓名" type="string" defaultValue="" not-null="1"/>
	<item id="bh" alias="编号" type="string" defaultValue="3101120000002052696" fixed="true"/>
	<item id="sexCode" alias="性别" type="string" defaultValue="1" not-null="1">
		<dic id="chis.dictionary.gender" onlySelectLeaf="true"/>
	</item>
	<item id="birthday" alias="出生年月" type="datetime" xtype="datefield" not-null="1"/>
	<item id="id" alias="身份证号" type="string" defaultValue="110101198001010117" not-null="1"/>
	<item id="gzdw" alias="工作单位" type="string" defaultValue=""/>
	<item id="brdh" alias="本人电话" type="string" colspan="2" defaultValue="13262652355"/>
	<item id="lxrxm" alias="联系人姓名" type="string" defaultValue=""/>
	<item id="lxrdh" alias="联系人电话" type="string" defaultValue=""/>
	<item id="czlx" alias="常住类型" type="string" defaultValue="1" not-null="1">
		<dic >
			<item key="1" text="户籍"/>
			<item key="2" text="非户籍"/>
		</dic>
	</item>
	<item id="mzcode" alias="民族" type="string">
		<dic id="chis.dictionary.ethnic" onlySelectLeaf="true"/>
	</item>
	<item id="xxcode" alias="血型" type="string" defaultValue="1" >
		<dic id="chis.dictionary.blood" onlySelectLeaf="true"/>
	</item>
	<item id="rh" alias="RH" type="string" defaultValue="1" >
		<dic id="chis.dictionary.RHnegative" onlySelectLeaf="true"/>
	</item>
	<item id="whcd" alias="文化程度" type="string">
		<dic id="chis.dictionary.education" render="Tree" onlySelectLeaf="true"/>
	</item>
	<item id="zy" alias="职业" type="string">
		<dic >
			<item key="1" text="国家机关"/>
			<item key="2" text="专业技术人员"/>
		</dic>
	</item>
	<item id="hyzk" alias="婚姻状况" type="string">
		<dic id="chis.dictionary.maritals" render="Tree" onlySelectLeaf="true"/>
	</item>
	<item id="ylfyzffs" alias="医疗费用支付方式" type="string" defaultValue="1">
		<dic>
			<item key="1" text="自费"/>
			<item key="2" text="公费"/>
		</dic>
	</item>
	<item id="ywgms" alias="药物过敏史" type="string" defaultValue="1">
		<dic>
			<item key="1" text="无"/>
			<item key="2" text="青霉素"/>
			<item key="3" text="磺胺"/>
		</dic>
	</item>
	<item id="bls" alias="暴露史" type="string" defaultValue="1">
		<dic>
			<item key="1" text="无"/>
			<item key="2" text="化学品"/>
			<item key="3" text="毒物"/>
		</dic>
	</item>
	<item id="jb" alias="疾病" type="string" defaultValue="1" group="既往史">
		<dic>
			<item key="1" text="无"/>
			<item key="2" text="高血压"/>
			<item key="3" text="糖尿病"/>
			<item key="4" text="冠心病"/>
			<item key="5" text="其他"/>
		</dic>
	</item>
	<item id="ss" alias="手术" type="string" defaultValue="1" group="既往史">
		<dic>
			<item key="1" text="无"/>
			<item key="2" text="有"/>
		</dic>
	</item>
	<item id="ws" alias="外伤" type="string" defaultValue="1" group="既往史">
		<dic>
			<item key="1" text="无"/>
			<item key="2" text="有"/>
		</dic>
	</item>
	<item id="sx" alias="输血" type="string" defaultValue="1" group="既往史">
		<dic>
			<item key="1" text="无"/>
			<item key="2" text="有"/>
		</dic>
	</item>
	
	<item id="fqs" alias="父亲" type="string" defaultValue="1" group="家庭史">
		<dic>
			<item key="1" text="无"/>
			<item key="2" text="高血压"/>
			<item key="3" text="糖尿病"/>
			<item key="4" text="冠心病"/>
			<item key="5" text="慢性阻塞性肺病"/>
			<item key="6" text="恶性肿瘤"/>
			<item key="7" text="其他"/>
		</dic>
	</item>
	<item id="mqs" alias="母亲" type="string" defaultValue="1" group="家庭史">
		<dic render="LovCombo">
			<item key="1" text="无"/>
			<item key="2" text="高血压"/>
			<item key="3" text="糖尿病"/>
			<item key="4" text="冠心病"/>
			<item key="5" text="慢性阻塞性肺病"/>
			<item key="6" text="恶性肿瘤"/>
			<item key="7" text="其他"/>
		</dic>
	</item>
	<item id="xdjms" alias="兄弟姐妹" type="string" defaultValue="1" group="家庭史">
		<dic render="LovCombo">
			<item key="1" text="无"/>
			<item key="2" text="高血压"/>
			<item key="3" text="糖尿病"/>
			<item key="4" text="冠心病"/>
			<item key="5" text="慢性阻塞性肺病"/>
			<item key="6" text="恶性肿瘤"/>
			<item key="7" text="其他"/>
		</dic>
	</item>
	<item id="zns" alias="子女" type="string" defaultValue="1" group="家庭史">
		<dic render="LovCombo">
			<item key="1" text="无"/>
			<item key="2" text="高血压"/>
			<item key="3" text="糖尿病"/>
			<item key="4" text="冠心病"/>
			<item key="5" text="慢性阻塞性肺病"/>
			<item key="6" text="恶性肿瘤"/>
			<item key="7" text="其他"/>
		</dic>
	</item>
	<item id="ycbs" alias="遗传病史" type="string" defaultValue="1">
		<dic>
			<item key="1" text="无"/>
			<item key="2" text="有"/>
		</dic>
	</item>
	<item id="cjqk" alias="残疾情况" type="string" defaultValue="1">
		<dic render="LovCombo">
			<item key="1" text="无残疾"/>
			<item key="2" text="视力残疾"/>
			<item key="3" text="听力残疾"/>
			<item key="4" text="言语残疾"/>
			<item key="5" text="肢体残疾"/>
			<item key="6" text="智力残疾"/>
			<item key="7" text="精神残疾"/>
			<item key="8" text="其他残疾"/>
		</dic>
	</item>
	<item id="cfpfss" alias="厨房排风设施" type="string" defaultValue="1" group="生活环境">
		<dic>
			<item key="1" text="无"/>
			<item key="2" text="油烟机"/>
			<item key="3" text="换气扇"/>
			<item key="4" text="烟筒"/>
		</dic>
	</item>
	<item id="rllx" alias="燃料类型" type="string" defaultValue="1" group="生活环境">
		<dic>
			<item key="1" text="液化气"/>
			<item key="2" text="煤"/>
			<item key="3" text="天然气"/>
			<item key="4" text="沼气"/>
			<item key="5" text="柴火"/>
			<item key="6" text="其他"/>
		</dic>
	</item>
	<item id="ys" alias="饮水" type="string" defaultValue="1" group="生活环境">
		<dic>
			<item key="1" text="自来水"/>
			<item key="2" text="经净化过滤的水"/>
			<item key="3" text="井水"/>
			<item key="4" text="河湖水"/>
			<item key="5" text="塘水"/>
			<item key="6" text="其他"/>
		</dic>
	</item>
	<item id="cs" alias="厕所" type="string" defaultValue="1" group="生活环境">
		<dic>
			<item key="1" text="卫生厕所"/>
			<item key="2" text="一格或二格粪池式"/>
			<item key="3" text="马桶"/>
			<item key="4" text="露天粪坑"/>
			<item key="5" text="简易棚厕"/>
		</dic>
	</item>
</entry>
