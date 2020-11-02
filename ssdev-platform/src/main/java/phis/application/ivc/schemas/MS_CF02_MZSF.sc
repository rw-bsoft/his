<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="MS_CF02" alias="门诊处方_门诊收费">
	<item id="SBXH" alias="识别序号" length="18" not-null="1" display="0" generator="assigned" pkey="true" />
	<item id="YPXH" alias="药品序号" length="18" not-null="1" display="0"/>
	<item id="CFLX" alias="单据类型" display="0"/>
	<item id="DJLX_text" alias="单据类型" display="0"/>
	<item id="YPZH" alias=" " type="long" width="20" renderer="showColor"/>
	<item ref="b.YPMC"/>
	<item ref="c.CFHM" display="0"/>
	<item ref="c.KFRQ" display="0"/>
	<item ref="c.CFLX" display="0"/>
	<item ref="c.KSDM" display="0"/>
	<item ref="c.YSDM" display="0"/>
	<item ref="c.DJLY" display="0"/>
	<item ref="c.FPHM" display="0"/>
	<item ref="c.DJYBZ" display="0"/>
	<item ref="c.YFSB" display="0"/>
	<item id="YPCD" alias="药品产地" type="long"  length="18" not-null="1">
  		<dic id="phis.dictionary.medicinePlace" />
 	</item>
	<item id="YFDW" alias="单位" type="string" length="4"/>
	<item id="CFSB" alias="处方识别" type="long" length="18" />
	<item id="YFGG" alias="规格" type="string" length="20"/>
	<item id="YPDJ" alias="单价" type="double" length="12" precision="4" not-null="1"/>
	<item id="YPSL" alias="数量" type="double" length="10" precision="2" not-null="1"/>
	<!--做到底层的时候金额加载加上事件,计算,现在界面设计暂时没做-->
	<item id="HJJE" alias="金额" type="double" length="10" width="100" precision="2"/>
	<item id="ZFBL" alias="自负比例" type="double" length="6" precision="3" not-null="1"/>
	<item id="YPYF" alias="用法" type="string" display="0" length="18">
  		<dic id="phis.dictionary.useRate" searchField="text" fields="key,text,MRCS" autoLoad="true"/>
  	</item>
	<item id="FYGB" alias="费用归并" type="long" length="18" display="0"/>
	<item id="CFTS" alias="处方贴数" type="int" length="2" display="0" not-null="1"/>
	<item id="SFJG" alias="审方结果" type="int" display="0" />
	<item id="YCSL" alias="一次数量" type="string" length="20" display="0"/>
	<item id="ZFPB" alias="自负判别" type="int" length="1" display="0"/>
	<item id="XMSPBH" alias="项目审批编号" type="string" length="15" display="0"/>
	<relations>
		<relation type="parent" entryName="phis.application.cfg.schemas.YK_TYPK" >
			<join parent="YPXH" child="YPXH" />
		</relation>	
		<relation type="child" entryName="phis.application.cic.schemas.MS_CF01" >
			<join parent="CFSB" child="CFSB" />
		</relation>		
	</relations>
</entry>
