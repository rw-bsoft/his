<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="YK_YPXX" alias="药库药品信息" sort="b.TYPE,a.YPXH">
	<item id="TP" alias="" type="string" renderer="onRenderer" width="23" length="20" virtual="true" display="1"/>
	<item ref="b.YPMC" fixed="true" layout="JBXX" />
	<item ref="b.TYMC" fixed="true" layout="JBXX" display="0" />	
	<item ref="b.YPGG" fixed="true" layout="JBXX"/>
	<item ref="b.YPDW" fixed="true" layout="JBXX"/>
	<item ref="b.TYPE" fixed="true" layout="JBXX" queryable="true"/>
	<item ref="b.ZBLB" fixed="true" layout="JBXX"/>
	<item ref="b.YPSX" fixed="true" layout="JBXX"/>
	<item ref="b.YPJL" layout="JBXX"/>
	<item ref="b.JLDW" layout="JBXX"/>
	<item ref="b.YCJL" layout="JBXX"/>
	<item ref="b.YPDC" fixed="true" layout="JBXX"/>
	<item ref="b.YPXQ" fixed="true" layout="JBXX" display="0"/>
	<item ref="b.ABC" fixed="true" layout="JBXX" display="0"/>
	<item ref="b.YPDM" fixed="true" layout="JBXX"/>
	<item ref="b.PSPB" fixed="true" layout="JBXX"/>
	<item ref="b.PYDM" fixed="true" layout="JBXX"/>
	<item ref="b.WBDM" fixed="true" layout="JBXX"/>
	<item ref="b.JXDM" fixed="true" layout="JBXX"/>
	<item ref="b.BHDM" fixed="true" layout="JBXX"/>
	<item ref="b.QTDM" fixed="true" layout="JBXX"/>
	<!--<item ref="b.JBYWBZ" fixed="true" layout="JBXX"/>-->
	<item ref="b.JYLX" fixed="true" layout="JBXX"/>
	<item ref="b.ZXDW" fixed="true" layout="YPBZ"/>
	<item ref="b.YFGG" fixed="true" layout="YPBZ"/>
	<item ref="b.YFDW" fixed="true" layout="YPBZ"/>
	<item ref="b.YFBZ" fixed="true" layout="YPBZ"/>
	<item ref="b.ZXBZ" fixed="true" layout="YPBZ"/>
	<item ref="b.BFGG" fixed="true" layout="YPBZ"/>
	<item ref="b.BFDW" fixed="true" layout="YPBZ"/>
	<item ref="b.BFBZ" fixed="true" layout="YPBZ"/>
	<item ref="b.FYFS" fixed="true"  layout="QT"/>
	<item ref="b.GYFF" fixed="true" layout="QT"/>
	<item ref="b.TSYP" fixed="true" layout="QT"/>
	<item ref="b.YPZC" fixed="true" layout="QT"/>
	<item ref="b.YBFL" fixed="true" layout="QT"/>
	<item ref="b.CFYP" fixed="true" layout="QT"/>
	<item ref="b.QZCL" fixed="true" layout="QT"/>
	<item ref="b.JSBZ" fixed="true" layout="QT"/>
	<item ref="b.XNXGBZ" fixed="true" layout="QT"/>
	<item ref="b.ZYJMBZ" fixed="true" layout="QT"/>
	<item ref="b.KSBZ" fixed="true" layout="KSS"/>	
	<item ref="b.YCYL" fixed="true" layout="KSS"/>
	<item ref="b.KSSDJ" fixed="true" layout="KSS"/>	
	<item ref="b.DDDZ" fixed="true" layout="KSS"/>	
	<item ref="b.YQSYFS" fixed="true" layout="KSS"/>	
	<item ref="b.SFSP" fixed="true" layout="KSS"/>	
	<item ref="b.ZSSF" fixed="true" layout="KSS"/>	
	<item ref="b.ZFYP" fixed="true" layout="KSS"/>	
	<item id="JGID" alias="机构ID" length="20" not-null="1"  display="0" 
		defaultValue="%user.manageUnit.id"  layout="GDC" update="false" pkey="true" />
	<item id="YPXH" alias="药品序号" length="18" type="long"  display="0"
		not-null="1" pkey="true" layout="GDC" >	
	</item>
	<item id="YKSB" alias="药库识别" length="18" display="0" type="long" not-null="1" layout="GDC"/>
	<item id="GCSL" alias="高储数量"  type="double" length="11" display="2" maxValue="999999.99" precision="2" defaultValue="0"
		not-null="1" layout="GDC"/>
	<item id="DCSL" alias="低储数量" type="double"  length="11" display="2" maxValue="999999.99" precision="2" defaultValue="0"
		not-null="1" layout="GDC"/>
	<item id="KWBM" alias="库位编码" type="string" display="0" length="16" layout="GDC"/>
	<item id="YKZF" alias="药库作废" type="int" length="1" display="0" not-null="1" layout="GDC" defaultValue="0" update="false">
	</item>
	<item id="CFLX" alias="处方类型" length="2" type="int" display="2" not-null="1" layout="GDC" defaultValue="1">
		<dic id="phis.dictionary.prescriptionType" />
	</item>
	<relations>
		<relation type="parent" entryName="phis.application.mds.schemas.YK_TYPK" >
			<join parent="YPXH" child="YPXH" />
		</relation>
	</relations>

</entry>
