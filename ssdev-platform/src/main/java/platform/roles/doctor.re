<?xml version="1.0" encoding="UTF-8"?>

<role id="platform.doctor" name="市一医院管理员" parent="platform.base" pageCount="4">
	<accredit>
		<apps acType="whitelist">
			<app id="platform.monitor.MONITOR">
				<catagory id="MONITOR_CATA" name="监控管理">
					<module id="MONITOR_01" name="数据采集报表" script="platform.monitor.client.QuaMain">
						<others/>
					</module>
				</catagory>
			</app>
		</apps>
		<storage acType="whitelist">
			<store id="platform.monitor.schemas.QualityData_Main" acValue="1111">
				<conditions>
					<condition type="filter">['eq',['$','Authororganization'],['s','47011661433010211A1001']]</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="platform.monitor.schemas.QualityData_Error" acValue="1111">
				<conditions>
					<condition type="filter">['eq',['$','Authororganization'],['s','47011661433010211A1001']]</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<store id="platform.monitor.schemas.QualityData_ErrorNumb" acValue="1111">
				<conditions>
					<condition type="filter">['eq',['$','Authororganization'],['s','47011661433010211A1001']]</condition>
				</conditions>
				<items>
					<others acValue="1111" />
				</items>
			</store>
			<others acValue="1111" />
		</storage>
	</accredit>
</role>
