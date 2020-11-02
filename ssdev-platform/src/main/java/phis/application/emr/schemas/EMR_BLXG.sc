<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="EMR_BLXG" alias="病历修改痕迹表" sort="XGSJ desc">
	<item id="JLXH" alias="记录序号" type="long" length="18" not-null="1"  display="0" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId"  type="increase" length="18" />
		</key>
	</item>
	<item id="BLBH" alias="病历编号" type="long" display="0" length="18" not-null="1"/>
	<item id="XGGH" alias="修改人" length="8">
		<dic id="phis.dictionary.doctor" autoLoad="true"/>
	</item>
	<item id="XGSJ" alias="修改时间" type="timestamp" width="140"/>
	<item id="HJNR" alias="痕迹内容" display="0" type="object"/>
	<item id="JGID" alias="机构ID" type="string" display="0" length="20" not-null="1"/>
</entry>
