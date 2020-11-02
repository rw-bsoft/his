<?xml version="1.0" encoding="UTF-8"?>

<role id="gp.system" name="系统管理员" parent="gp.base">
	<accredit>
		<apps>
			<app id="gp.application.fd.FAMILYDOCTOR">
				<others />
			</app>
			<app id="gp.application.fda.FAMILYDOCTOR">
				<others />
			</app>
			<app id="gp.application.cm.FAMILYDOCTOR">
				<others />
			</app>
			<app id="gp.application.systemmanage.SYSTEMMANAGE">
				<others />
			</app>
			<app id="chis.application.systemmanage.SYSTEMMANAGE">
				<others />
			</app>
			<app id="chis.application.healthmanage.HEALTHMANAGE">
				<catagory id="HR">
					<module id="B05">
						<others/>
					</module>
					<module id="B051">
						<others/>
					</module>
				</catagory>
				<catagory id="MOV">
					<module id="R01">
						<others/>
					</module>
					<module id="R01_1">
						<others/>
					</module>
					<module id="R04">
						<others/>
					</module>
					<module id="R04_1">
						<others/>
					</module>
					<module id="R04_2">
						<others/>
					</module>
					<module id="R04_3">
						<others/>
					</module>
					<module id="R05">
						<others/>
					</module>
					<module id="R05_1">
						<others/>
					</module>
					<module id="R05_2">
						<others/>
					</module>
					<module id="R05_3">
						<others/>
					</module>
				</catagory>
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
