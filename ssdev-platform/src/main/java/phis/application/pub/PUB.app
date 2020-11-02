<?xml version="1.0" encoding="UTF-8"?>
<application id="phis.application.pub.PUB" name="公共功能"
	type="1">
	<catagory id="PUB" name="公共功能">
		<module id="WardSwitch" name="病区切换"
			script="phis.application.top.script.DepartmentSwitchList">
			<properties>
				<p name="entryName">phis.application.pub.schemas.GY_QXKZ_BQ</p>
				<p name="departmentType">4</p>
			</properties>
		</module>
		<module id="StoreHouseSwitch" name="药库切换"
			script="phis.application.top.script.PharmacySwitchList">
			<properties>
				<p name="entryName">phis.application.pub.schemas.YK_YKLB_PUB</p>
				<p name="departmentType">5</p>
			</properties>
		</module>
		<module id="PharmacySwitch" name="药房切换"
			script="phis.application.top.script.PharmacySwitchList">
			<properties>
				<p name="entryName">phis.application.pub.schemas.YF_YFLB_PUB</p>
				<p name="departmentType">1</p>
			</properties>
		</module>
		<module id="TreasurySwitch" name="库房切换"
			script="phis.application.top.script.TreasurySwitchList">
			<properties>
				<p name="entryName">phis.application.pub.schemas.WL_KFXX_PUB</p>
				<p name="departmentType">6</p>
			</properties>
		</module>
		<module id="DepartmentSwitch_out" name="门诊科室切换"
			script="phis.application.top.script.DepartmentSwitchList">
			<properties>
				<p name="entryName">phis.application.pub.schemas.GY_QXKZ_PUB</p>
				<p name="departmentType">2</p>
			</properties>
		</module>
		<module id="DepartmentSwitch_in" name="住院科室切换"
			script="phis.application.top.script.DepartmentSwitchList">
			<properties>
				<p name="entryName">phis.application.pub.schemas.GY_QXKZ_PUB</p>
				<p name="departmentType">3</p>
			</properties>
		</module>
		<module id="MedicalSwitch" name="医技科室切换"
			script="phis.application.top.script.MedicalSwitchList">
			<properties>
				<p name="entryName">phis.application.pub.schemas.GY_QXKZ_YJ</p>
				<p name="departmentType">9</p>
			</properties>
		</module>
	</catagory>
</application>