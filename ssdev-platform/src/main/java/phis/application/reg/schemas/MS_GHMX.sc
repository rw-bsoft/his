<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_GHMX" alias="挂号明细表">
	<item id="SBXH" alias="识别序号" length="18" display="0" type="long" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId"  type="increase" length="18" startPos="1000"/>
		</key>
	</item>
	<item id="JGID" alias="机构ID" length="8" display="0" />
	<item id="BRID" alias="病人ID号" type="long" length="18" display="0" />
	<item ref="b.EMPIID" alias="empiId"  />
	<item ref="b.MZHM" alias="门诊号码" type="string" length="32" width="100" not-null="1" queryable="true" selected="true"/>
	<item ref="b.BRXZ" alias="病人性质" fixed="true" length="18" />
	<item ref="b.BRXM" alias="病人姓名" fixed="true" type="string" length="40" />
	<item ref="b.BRXB" alias="病人性别" fixed="true" length="4"/>
  
	<item id="GHSJ" alias="挂号时间" fixed="true" defaultValue="%server.date.date" type="timestamp" width="150"/>
	<item id="GHLB" alias="挂号类别" type="int" fixed="true" length="4">
		<dic>
			<item key="1" text="普通门诊"/>
			<item key="2" text="急诊门诊"/>
		</dic>
	</item>
	<item id="KSDM" alias="科室代码" display="0" type="long" length="18" />
	<item id="KSMC" alias="挂号科室" virtual="true" type="string" length="18" display="0"/>
	<item id="YSDM" alias="医生代码" display="0" type="string" length="10"/>
	<item id="JZYS" alias="医生" type="string" length="10" display="0"/>
	<item id="GHJE" alias="挂号费" type="double" length="10" precision="2" not-null="1" display="0"/>
	<item id="ZLJE" alias="诊疗费" type="double" length="10" precision="2" not-null="1" display="0"/>
	<item id="ZJFY" alias="专家费" type="double" length="10" precision="2" not-null="1" display="0"/>
	<item id="JZHM" alias="就诊号码" fixed="true" type="string" length="20" display="0"/>
	<item id="JZZT" alias="就诊状态" type="int" length="1" display="0"/>
	<item id="MZLB" alias="门诊类别" type="long" length="18" display="0"/>
	<item id="CZSJ" alias="操作时间" type="timestamp" display="0"/>
	<item id="NHDJSJ" alias="农合登记时间" type="timestamp" display="0"/>
	<item id="NHDJID" alias="农合登记ID" type="string" display="0"/>
	<item id="ZJYYBZ" alias="紫金预约标志" type="string" length="1" display="0"/>
	<item id="QHSJ" alias="取号时间" type="timestamp" display="0"/>
	<item id="ZJYYID" alias="紫金预约挂号ID" type="long" length="18" display="0"/>
	<item id="NJJBLSH" alias="南京金保流水号" type="string" display="0"/>
	<item id="SFFS" alias="收费方式" length="1" type="int" display="0"/>
	<item id="FFFS" alias="付款方式" length="2" type="int" display="0"/>
	<item id="YHJE" alias="优惠金额" length="12" type="double" precision="2" not-null="1" display="0"/>
	<item id="YHJEINIT" alias="优惠金额初始值" length="12" type="double" precision="2" not-null="1"/>
	<item id="JYJMBZ" alias="家医减免标志" display="0" type="string" length="20"/>
	<relations>
		<relation type="parent" entryName="phis.application.cic.schemas.MS_BRDA">
			<join parent="BRID" child="BRID" />
		</relation>
	</relations>
</entry>
