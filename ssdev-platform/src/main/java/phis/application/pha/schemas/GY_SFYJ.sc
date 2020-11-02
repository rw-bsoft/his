<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="GY_SFYJ" alias="公用_审方意见">
	<item id="JLXH" alias="记录序号" type="long" not-null="1" display="0" generator="assigned" pkey="true" />
	<item id="SFLX" alias="审方类型" type="long" length="8" display="0">
		<dic>
			<item key="1" text="书写规范" />
  			<item key="2" text="用药适宜性" />
		</dic>
	</item>
	<item id="SFYJ" alias="审方意见"  type="string" length="255" width="600" />
</entry>