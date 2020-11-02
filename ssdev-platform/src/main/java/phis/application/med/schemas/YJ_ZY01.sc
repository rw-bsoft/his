<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="YJ_ZY01" tableName="YJ_ZY01" alias="住院医技01表">
	<item id="YJXH" alias="医技序号"  length="18" type="long" not-null="1" display="0" generator="assigned" pkey="true">
		<key>
	      <rule name="increaseId" type="increase" length="18" startPos="1"/>
	    </key>
	</item>
	<item id="JGID" alias="机构ID" type="long" length="20" not-null="1" fixed="true"/>
	<item id="ZYH" alias="住院号" type="long" length="18"  fixed="true"/>
	<item id="ZYHM" alias="住院号码" type="string" length="10"  fixed="true"/>
	<item id="BRXM" alias="病人姓名" type="string" length="40" />
	<item id="KDRQ" alias="开单日期" type="date"  />
	<item id="TJSJ" alias="提交时间" type="date"  />
	<item id="KSDM" alias="科室代码" type="long" length="18"  fixed="true"/>
	<item id="YSDM" alias="医生代码" type="string" length="10"  fixed="true"/>
	<item id="ZXKS" alias="执行科室" type="long" length="18"  fixed="true"/>
	<item id="HJGH" alias="划价工号" type="string" length="10"  fixed="true"/>
	<item id="DJZT" alias="单据状态" type="long" length="1" not-null="1"/>
	<item id="FYBQ" alias="费用病区" type="long" length="18"/>
	<item id="TJHM" alias="特检号码" type="string" length="10"  fixed="true"/>
	<item id="ZXRQ" alias="执行日期" type="date"   fixed="true"/>
	<item id="ZXYS" alias="执行医生" type="string" length="10"  fixed="true"/>
	<item id="ZXPB" alias="执行判别" type="long" length="1"  fixed="true" not-null="1"/>
	<item id="BBBM" alias="标本编码" type="string" length="4"  fixed="true"/>
	<item id="ZYSX" alias="注意事项" type="string" length="250"/>
	<item id="ZFPB" alias="作废判别" type="long" length="1"  fixed="true" not-null="1"/>
	<item id="HYMX" alias="化验明细" type="string" length="250"/>
	<item id="YJPH" alias="医技片号" type="string" length="20" fixed="true"/>
	<item id="SQDH" alias="申请单号" type="long" length="18"/>
	<item id="BWID" alias="部位编号" type="long" length="9"/>
	<item id="JBID" alias="疾病编号" type="long" length="18"/>
	<item id="SQWH" alias="申请文号" type="long" length="18"/>
</entry>