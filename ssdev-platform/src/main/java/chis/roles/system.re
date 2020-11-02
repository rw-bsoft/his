<?xml version="1.0" encoding="UTF-8"?>

<role id="chis.system" name="系统管理员" parent="chis.base" type="Tsystem">
	<accredit>
		<apps acType="blacklist">
			<app id="gp.application.index.CENTER">
				<others/>
			</app>
		</apps>
		<storage acType="whitelist">       
			<others acValue="1111"/>
		</storage>
		<reminderList acType="whitelist">
			<reminder id="1" />
			<reminder id="2" />
			<reminder id="4" />
			<reminder id="10" />
		</reminderList>
	</accredit>
</role>
