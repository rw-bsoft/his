<entry entityName="chis.application.pub.schemas.PUB_BaseDrug" alias="药品信息" sort="a.YPXH desc" >
	<!-- 药品基本信息 -->
	<item id="YPXH" alias="药品内码" type="long" length="18" not-null="1" display="0"
		generator="assigned" pkey="true"  layout="JBXX" >
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16"
				startPos="8869" />
		</key>
	</item>
	<item id="YPMC" alias="药品名称" type="string" width="180" anchor="100%"
		length="80" colspan="2" not-null="true" layout="JBXX"/>
	<item id="YPGG" alias="规格" type="string" length="10"  layout="JBXX"/>
	<item id="YPDW" alias="单位" type="string" length="2" layout="JBXX" not-null="1"/>
	<item id="ZXDW" alias="最小单位" type="string" length="2" display="2" layout="YPBZ"/>
	<item id="ZXBZ" alias="最小包装" type="int" length="4" defaultValue="1" display="2"  not-null="1" layout="YPBZ" minValue="0"/>
	<item id="TYPE" alias="类别" display="2"  not-null="1" defaultValue="1"
		type="int" length="2" layout="JBXX" queryable="true">
		<dic id="chis.dictionary.prescriptionType"/>
	</item>
	<item id="YPDM" alias="药品类型" type="string"  length="10" layout="JBXX" display="2" >
		<!-- <dic id="medicinesCode" render="Tree"></dic> -->
	</item>
	<item id="SXMC" alias="剂型名称" type="string"  width="300" anchor="100%" length="7" not-null="true" colspan="3"/>
	<item id="PYDM" alias="拼音码" type="string" length="10" selected="true" target="YPMC" codeType="py"
		queryable="true" layout="JBXX">
	</item>
	<item id="YPXQ" alias="有效期" type="int" length="6" display="0" layout="JBXX"/>
</entry>