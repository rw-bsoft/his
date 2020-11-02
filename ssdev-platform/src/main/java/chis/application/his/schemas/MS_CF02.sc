<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_CF02" alias="门诊处方" sort="c.KFRQ">
	<item id="SBXH" alias="识别序号" length="18" not-null="1" display="0"  type="long" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="16"
				startPos="1000" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" display="0" type="string" length="20" />
	<item id="HJJE" alias="合计金额" display="0" type="double" length="12" />
	<item id="CFSB" alias="处方识别" display="0" type="long" length="18" not-null="1"  />
	<item id="YPXH" alias="药品序号" type="long"  display="0" length="18" not-null="1"/>
	<item id="YPCD" alias="药品产地" type="long"  display="0"  length="18" not-null="1">
		<dic id="phis.dictionary.medicinePlace" />
	</item>
	<item ref="c.KFRQ" type="datetime" width="150"/>
	<item id="XMLX" alias="项目类型" type="string" length="2" not-null="1">
		<dic id="phis.dictionary.prescriptionType" />
	</item>
	<item ref="b.YPMC"/>
	<item id="YCJL" alias="剂量" type="double" length="10" precision="2"/>
	<item id="YFGG" alias="规格" type="string" length="20"/>
	<item id="YFDW" alias="单位" type="string" length="4" width="60"/>
	<item id="YPYF" alias="频次" type="string" length="18" width="50">
		<dic id="phis.dictionary.useRate" searchField="MRCS"/>
	</item>
	<item id="GYTJ" alias="用法" type="int" length="4">
		<dic id="phis.dictionary.drugMode" />
	</item>
	<item id="YYTS" alias="天数" type="int" width="50"/>
	<item id="YPSL" alias="总量" type="double" length="10" precision="2" not-null="1" width="60"/>
	<item id="CFTS" alias="贴数" type="int" length="2" not-null="1" width="50"/>
	<item ref="c.YSDM"/>
	<item ref="c.FPHM" display="0"/>
	<item ref="c.ZFPB" display="0"/>
	<item id="BZXX" alias="备注信息" length="100" type="string" />
	<item id="SFSF" alias="是否收费" length="20" type="string" virtual="true"/>
	
	<item id="YPDJ" alias="药品单价" length="12" type="double" display="0" precision="4" />
	<item id="YPZS" alias="服法" type="int" not-null="1" display="0" defaultValue="1">
		<dic id="phis.dictionary.suggested"/>
	</item>
	<item id="YCSL" alias="一次数量" type="string" length="20" display="0" not-null="1"/>
	<item id="FYGB" alias="费用归并" type="long" display="0" length="18" not-null="1"/>
	<item id="ZFBL" alias="自负比例" type="double" display="0" length="6" precision="3" not-null="1"/>
	<item id="YPZH" alias="药品组号" type="long" length="18" display="0"/>
	<item id="YFBZ" alias="药房包装" type="int" length="4" not-null="1" display="0"/>
	<item id="SJYL" alias="实际用量" type="string" length="20" display="0"/>
	<item id="PSPB" alias="皮试判别" type="int" length="1" display="0"/>
	<item id="YCSL2" alias="一次用量" type="double" length="10" display="0" precision="4"/>
	<item id="XSSL" alias="显示数量" type="double" length="10" display="0" precision="4"/>
	<item id="MRCS" alias="每日次数" type="int" length="2" not-null="1" display="0"/>
	<item id="CFBZ" alias="处方标志" type="string" display="0" length="40"/>
	
	
	<item id="PSJG" alias="皮试结果" type="int" display="0" length="1"/>
	<item id="PLXH" alias="排列序号" type="int" display="0" length="4"/>
	<item id="SYBZ" alias="输液标志" type="int" display="0" length="1"/>
	
	<item id="BZMC" alias="备注名称"  display="0" type="string" />
	<item id="SFJG" alias="审方结果"  type="int" display="0" />
	<item id="SFGH" alias="审方工号" display="0" type="string" />
	<item id="SFYJ" alias="审核意见" display="0" type="string" />
	<item id="SPBH" alias="审批编号" display="0" type="long" length="15"/>
	<relations>
		<relation type="parent" entryName="phis.application.cfg.schemas.YK_TYPK" >
			<join parent="YPXH" child="YPXH" />
		</relation>	
		<relation type="child" entryName="phis.application.cic.schemas.MS_CF01" >
			<join parent="CFSB" child="CFSB" />
		</relation>		
	</relations>
</entry>
