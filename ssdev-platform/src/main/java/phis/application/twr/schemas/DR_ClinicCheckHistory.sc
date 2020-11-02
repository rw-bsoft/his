<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="DR_CLINICCHECKHISTORY" tableName="DR_CLINICCHECKHISTORY" alias="检查申请记录">
	<!--(目前暂时先放在本地数据库)-->
	<item id="ID" alias="记录序号" type="string" length="16"  not-null="1" generator="assigned" pkey="true" width="160" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>  
	</item>
	<item id="EMPIID" alias="EMPIID" type="string" length="32" hidden="true" />
	<item id="MZHM" alias="门诊号码" type="string" length="32" hidden="true"/>
	<item id="JIUZHENKH" alias="就诊卡号" length="10" type="string"  hidden="true"/>
	<item id="BINGRENXM" alias="病人姓名" type="string" length="32"/>
	<item id="SHENQINGRQ" alias="申请日期" length="12" type="string" />
	<item id="SONGJIANRQ" alias="送检日期" type="date" length="20"  update="false" defaultValue="%server.date.date" not-null="true" />
	<item id="ZHENDUAN" alias="诊断" length="150" width="150" type="string" />
	<item id="BINGRENTZ" alias="病人体征" length="200" width="150" type="string" />
	<item id="BINGQINGMS" alias="病情描述" length="300" width="150" type="string" />
	<item id="JIANCHASQDH" alias="检查申请单号" length="20" width="120"  type="string" hidden="true"/>
	<item id="STATUS" alias="送检状态" length="2" type="string" >
		<dic>
			<item key="0" text="未送检"/>
			<item key="1" text="已送检"/>
		</dic>
	</item>
	<item id="SONGJIANKSMC" alias="送检机构" length="10" width="180" type="string" />
	<item id="SONGJIANKS" alias="送检机构id" type="string" length="16"   hidden="true" />
	<item id="SONGJIANYS" alias="送检科室" length="20" type="string" />
	<item id="JIANCHAXMMC" alias="送检项目" length="20" type="string" />
	<item id="JIUZHENKLX" alias="就诊卡类型" length="10" type="string" hidden="true"/>
	<item id="SHOUFEISB" alias="收费识别" length="5" type="string" hidden="true"/>
	<item id="QITAJC" alias="其它检查" length="100" type="string" hidden="true" />
	<item id="BINGRENZS" alias="病人主诉" length="100" type="string" hidden="true"/>
	<item id="JIANCHALY" alias="检查来源" length="10" type="string" hidden="true"/>
	<item id="BINGRENSFZH" alias="病人身份证号" length="18" type="string" hidden="true"/>
	<item id="JIESHOUFS" alias="接收方式" length="10" type="string" hidden="true"/>
	
</entry>
