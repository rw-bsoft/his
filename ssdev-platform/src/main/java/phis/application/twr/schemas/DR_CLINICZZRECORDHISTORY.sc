<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="DR_CLINICZZRECORDHISTORY" tableName="DR_CLINICZZRECORDHISTORY" alias="转诊信息详细记录">
	<!--(目前暂时先放在本地数据库)-->
	<item id="ID" alias="记录序号" type="string" length="16"  not-null="1" generator="assigned" pkey="true" width="160" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>  
	</item>
	<item id="EMPIID" alias="EMPIID" type="string" length="32" display="0" />
	<item id="MZHM" alias="门诊号码" type="string" length="32"/>
	<item id="BINGRENXM" alias="病人姓名" type="string" length="32"/>
	<item id="GUAHAOXH" alias="挂号序号" type="string" length="32" />
	<item id="GUAHAOID" alias="挂号ID" type="string" length="32"/>
	<item id="YUYUERQ" alias="转诊日期" type="string" length="20" width="120" fixed="true"/>
	<item id="SHENQINGYS" alias="申请医生" type="string" length="30" fixed="true" />
	<item id="ZHUANRUYYMC" alias="转入医院名称" type="string" length="20" width="120" fixed="true"/>
	<item id="ZHUANRUKSMC" alias="转入科室名称" type="string" length="20" width="120" fixed="true"/>
	<item id="YISHENGXM" alias="接诊医生" type="string" length="20" fixed="true" />
	<item id="GUAHAOBC" alias="挂号班次" type="string" length="20" fixed="true" />
	<item id="JIUZHENSJ" alias="就诊时间" type="string" length="32" />
	<item id="ZHUANZHENDH" alias="转诊单号" type="string" length="32" />
	<item id="JIUZHENDD" alias="就诊地点" type="string" length="20" width="120" fixed="true"/>
	<item id="QUHAOMM" alias="取号密码" type="string" display="0" length="32"/>
</entry>
