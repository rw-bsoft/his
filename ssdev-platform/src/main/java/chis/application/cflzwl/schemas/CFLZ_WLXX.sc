<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.cflzwl.schemas.CFLZ_WLXX" alias="处方流转物流信息卡">
 <item id="ID" alias="处方流转编号" type="long" length="16" pkey="true"  not-null="1" fixed="true" hidden="true" generator="assigned" display="0"> 
    <key> 
      <rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/> 
    </key> 
 </item>
 <item id="WLZT" defaultValue="1" alias="状态" length="1" fixed="true">
  	 <dic>
      <item key="1" text="未缴费" />
      <item key="2" text="已缴费" />
      <item key="3" text="已发货" />
      <item key="4" text="已收货" />
      <item key="5" text="取消" />
    </dic>
 </item> 
    <item id="BRNAME" alias="姓名" fixed="true" />
	<item id="SFZH"  alias="身份证号码" vtype="idCard" length="18" />
	<item id="SEX" alias="性别" fixed="true"/>
	<item id="AGE" alias="年龄" fixed="true"/>
	<item id="PHONE" alias="联系电话*" not-null="1"/>
    <item id="ADDRESS" alias="收货地址*" length="500" not-null="1" colspan="2"/>
    <item id="PSPLACE" alias="配送区域" type="string" defaultValue="1">
	 <dic>
      <item key="1" text="本县区" />
      <item key="2" text="本市其他县区" />
      <item key="3" text="本省其它地市" />
      <item key="4" text="外省" />
      <item key="5" text="港澳台" />
      <item key="6" text="外籍" />
  	 </dic>
    </item>
  	<item id="CREATEORGID" alias="录入机构" type="string" length="20" width="180" fixed="true" update="false" defaultValue="%user.manageUnit.id">
	    <dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id" />
	    <set type="exp">['$','%user.manageUnit.id']</set>
  	</item>
	<item id="LZORGID" alias="上级医院id" display="0"/>
	<item id="LZORGNAME" alias="上级医院" fixed="true"/>
	<item id="KDYS" alias="开单医生" fixed="true" display="0"/>
	<item id="KDYSGH" alias="开单医生" type="string" length="20" fixed="true" update="false" defaultValue="%user.userId">
    	<dic id="phis.dictionary.user" parentKey="%user.manageUnit.id" render="Tree" onlySelectLeaf="true" keyNotUniquely="true"/>
    </item>
	<item id="FPHM" alias="发票号码" fixed="true"/>
  	<item id="WLDBH" alias="物流单号" type="string"  fixed="true" length="32"/>
  	<item id="KFRQ" alias="开方日期" type="datetime" fixed="true" defaultValue="%server.date.datetime"/>
  	<item id="DDRQ" alias="订单日期" type="datetime" fixed="true" defaultValue="%server.date.datetime"/>
  	<item id="FJRNAME" alias="寄件人姓名" type="String" fixed="true"/>
  	<item id="JJRPHONE" alias="寄件人电话" type="String" fixed="true"/>
  	<item id="KDGS" alias="快递公司" type="String" fixed="true"/>
  	<item id="KDYPHONE" alias="快递员电话" type="String" fixed="true"/>
  	<item id="FHRQ" alias="发件日期" type="datetime" fixed="true"/>
  	<item id="EMPIID" alias="人员主键" type="string" display="0" />
  	<item id="CFHM" alias="处方号码" type="string" fixed="true"/>
  	<item id="REMARK" alias="备注" type="string" colspan="2"/>
 </entry>