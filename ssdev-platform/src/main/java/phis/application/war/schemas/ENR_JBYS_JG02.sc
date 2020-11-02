<?xml version="1.0" encoding="UTF-8"?>
<entry alias="护理记录结构表" entityName="ENR_JBYS" >
	<item id="XMBH" alias="项目编号" type="long" not-null="1" length="18" generator="assigned" pkey="true" display="0"/>
	<item id="JGBH" alias="结构编号" type="long" not-null="1" length="9" display="0"/>
	<item id="YSBH" alias="元素编号" type="long" not-null="1" length="18" display="0"/>
	<item id="XMMC" alias="项目名称" type="string" not-null="1" length="30" display="0"/>
	<item id="XSMC" alias="显示名称" type="string" not-null="1" length="30" fixed="false" width="100"/>
	<item id="XMQZ" alias="项目取值" type="string" length="255" width = "110" renderer="yyxjy"/>
	<item id="KSLH" alias="开始列号" type="long" not-null="1" length="2" display="0"/>
	<item id="JSLH" alias="结束列号" type="long" not-null="1" length="2" display="0"/>
	<item id="HDBZ" alias="活动标志" type="long" not-null="1" length="1" display="0"/>
	<item id="YSKZ" alias="元素扩展" type="clob"  display="0"/>
	<item id="SJGS" alias="数据格式" type="string" length="20" display="0"/>
	<item id="ZCZSX" alias="正常值上限" type="double" length="18" display="0"/>
	<item id="ZCZXX" alias="正常值下限" type="double" length="18" display="0"/>
	<item id="YXZSX" alias="有效值上限" type="double" length="18" display="0"/>
	<item id="YXZXX" alias="有效值下限" type="double" length="18" display="0"/>
	<item id="SFBT" alias="是否必填" type="long" not-null="1" length="1" display="0"/>
	<item id="JZBJ" alias="禁止编辑" type="long" not-null="1" length="1" display="0"/>
	<item id="FZYT" alias="复制原态" type="long" not-null="1" length="1" display="0"/>
	<item id="XMDC" alias="项目导出" type="long" not-null="1" length="1" display="0"/>
	<item id="YMCLFS" alias="页面处理方式" type="long" not-null="1" length="1" display="0"/>
	<item id="HHJG" alias="换行间隔" type="long" length="2" display="0"/>
	<item id="yskz_bnt" alias="yskz_bnt" type="string"  length="2" display="0"/>
	<item ref="YSLX" alias="元素类型" type="long" not-null="1" length="2" display="0"/>
	<item ref="SJLX" alias="数据类型" type="long" not-null="1" length="2" display="0"/>
	<item id="XMKD" alias="项目宽度" type="timestamp" length="2" display="0"/>
	<item id="ZDYXM" alias="自定义项目" type="timestamp"  length="1" display="0"/>
	
	<relations>
		<relation type="parent" entryName="phis.application.war.schemas.ENR_JBYS" >
			<join parent="YSBH" child="YSBH"></join>
		</relation>
	</relations>
</entry>