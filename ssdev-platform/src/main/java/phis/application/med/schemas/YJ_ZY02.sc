<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="YJ_ZY02" tableName="YJ_ZY02" alias="住院医技02表">
	<item id="SBXH" alias="识别序号"  length="18" type="long" not-null="1" display="0" generator="assigned" pkey="true">
		<key>
	      <rule name="increaseId" type="increase" length="18" startPos="1"/>
	    </key>
	</item>
	<item id="JGID" alias="机构ID" type="long" length="20" not-null="1" fixed="true"/>
	<item id="YJXH" alias="医技序号" type="long" length="18"  fixed="true" not-null="1"/>
	<item id="YLXH" alias="医疗序号" type="long" length="6"  fixed="true" not-null="1"/>
	<item id="XMLX" alias="项目类型" type="long" length="2"  fixed="true"/>
	<item id="YJZX" alias="医技主项" type="long" length="1" />
	<item id="YLDJ" alias="医疗单价" type="double" length="10" not-null="1" />
	<item id="YLSL" alias="医疗数量" type="double" length="8" not-null="1" />
	<item id="FYGB" alias="费用归并" type="long" length="4"  fixed="true"/>
	<item id="ZFBL" alias="自负比例" type="double" length="5" not-null="1"/>
	<item id="YZXH" alias="医嘱序号" type="long" length="18"  fixed="true"/>
	<item id="YEPB" alias="婴儿唯一号" type="long" length="1"  fixed="true" />
	<!-- 
	<item id="TPLJ" alias="医技图片" type="string" length="50"  fixed="true"/>
	-->
	
</entry>