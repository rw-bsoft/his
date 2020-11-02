<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="SYS_Personnel" alias="员工代码" sort="personId">
	<item id="PERSONID" alias="员工工号" type="string" length="10"
		not-null="1" generator="assigned" pkey="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="10"
				startPos="1" />
		</key>
	</item>
	<item id="PERSONNAME" alias="员工姓名" type="string" length="50" />
	<item id="PYCODE" alias="拼音码" type="string" length="10" queryable="true"
		selected="true">
		<set type="exp" run="server">['py',['$','r.YGXM']]</set>
	</item>

	<item id="OFFICECODE" alias="科室名称" type="long" length="18"
		not-null="1" width="100">
		<dic id="phis.dictionary.department" />
	</item>
	<item id="JGID" alias="所属机构" length="50" fixed="true"
		width="150" >
		<dic id="phis.dictionary.organizationDic" />
	</item>
</entry>
