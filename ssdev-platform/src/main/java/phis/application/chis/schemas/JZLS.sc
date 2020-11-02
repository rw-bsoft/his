<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="JZLS" tableName="JZLS" alias="就诊历史" sort="KSSJ desc">
	<item id="JZXH" alias="就诊序号" length="18" type="string" not-null="1" hidden="true" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" type="increase" length="16" startPos="1000"/>
		</key>
	</item>
	<item id="IDCARD" asName="IDCARD" alias="身份证号" type="string"  width="150" length="18"  hidden="true" />
	<item id="KSSJ" alias="就诊时间" type="timestamp"  width="140"/>
	<item id="JGID" alias="就诊机构" length="25" width="180" type="string">
		<dic id="phis.@manageUnit"/>
	</item>
	<item id="SSY"  alias="收缩压" type="string" length="180"  />
	<item id="SZY"  alias="舒张压" type="string" length="180"  />
	<item id="XTK" alias="空腹血糖" type="string" length="180"  />
	<item id="XTC" alias="餐后血糖" type="string" length="180"  />
	<item id="FZJC"  alias="辅助检查" type="string" length="180"  />
	<!--<item id="YYMC"  alias="用药名称" type="string" length="180"  />
	<item id="YYC"  alias="用药每次" type="string" length="180"  />
	<item id="YYMR"  alias="用药每日" type="string" length="180"  />
	<item id="YYDW"  alias="用药单位" type="string" length="180"  />-->
</entry>
