<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_BQYZ" alias="病区医嘱">
	<item id="JLXH" alias="记录序号" type="long" length="18" not-null="1" display="0" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId"  type="increase" length="18"
				startPos="1000" />
		</key>
	</item>
    <item id="YPXH" alias="药品序号" type="long" length="18" not-null="1" display="0" />
    <item id="YZMC" alias="名称" type="string" mode="remote" length="100" width="200" virtual="true"/>
    <item id="YCJL" alias="剂量" type="double" length="8" precision="2" min="0.01" max="9999.99" not-null="true" defaultValue="1" virtual="true"/>
    <item id="JLDW" alias=" " display="1" width="30" fixed="true" virtual="true"/>
	<item id="JZ" alias="脚注" type="int" not-null="1">
		<dic id="phis.dictionary.suggestedCY" autoLoad="true"/>
	</item>
	<item id="YPJL" alias="药品剂量" type="string" display="0" virtual="true"/>
	<item id="YCSL" alias="数量" type="double" length="8" precision="2" defaultValue="1" display="0"/>
	<item id="YFDW" alias="单位"  type="string" display="0"/>
    <item id="YFBZ" alias="药房包装" type="int" display="0" />
    <item id="YPDJ" alias="单价" type="double" length="12" precision="4" min="0.0001" max="99999999.9999" nullToValue="0" not-null="true" width="70" display="0" />
    <item id="YPLX" alias="药品类型" type="int" defaultValue="-1" display="0" />
    <item id="YPCD" alias="产地" type="long" length="18" not-null="1" fixed="true" display="0" >
        <dic id="phis.dictionary.medicinePlace" autoLoad="true"/>
    </item>
    <item id="MRCS" alias="每日次数" type="int" display="0"/>
    <item id="YZZXSJ" alias="执行时间" type="string" length="80" fixed="true" display="0"/>
    <item id="SRCS" alias="首日次数" type="int" max="999999" not-null="true" length="6" defaultValue="0"/>
    <item id="FYFS" alias="发药方式" type="long" length="18" display="0" >
        <dic id="phis.dictionary.hairMedicineWay" autoLoad="true"/>
    </item>
   	<item id="SIGN" alias="库存标志" type="string" virtual="true" display="0"/>
   	<item id="msg" virtual="true" alias="提示信息" type="string"  display="0" />
   	<item id="SYPC" alias="频次"  not-null="true"  type="string" length="6" width="60" display="0">
		<dic id="phis.dictionary.useRate" searchField="text" fields="key,text,MRCS,ZXSJ" autoLoad="true"/>
	</item>
	<item id="XMLX" alias="项目类型" type="int"  display="0"/>
	<item id="YFSB" alias="发药药房" type="long" length="18" display="0" not-null="1">
		<dic id="phis.dictionary.wardPharmacy" autoLoad="true" />
	</item>
    <!--<item id="YPZS" alias="服法" type="int" not-null="1" virtual="true">
            <dic id="suggested"/>
        </item>-->
</entry>
