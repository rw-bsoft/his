<?xml version="1.0" encoding="UTF-8"?>

<role id="phis.78" name="抗菌药物管理" parent="base" pageCount="2"
	version="1388456433586">
	<accredit>
		<apps acType="whitelist">
			<app id="phis.application.menu.ZYGL">
				<catagory id="KJY" acType="whitelist">
					<module id="WAR38">
						<others />
					</module>
					<module id="WAR3801">
						<others />
					</module>
					<module id="WAR3802">
						<others />
					</module>
					<module id="WAR380201">
						<others />
					</module>
				</catagory>
				
			</app>
			<app id="phis.application.menu.TJFX">
			<catagory id="KJY"  acType="blacklist">
				<module id="WAR38">
					<others />
				</module>
			</catagory>
			</app>
			<app id="phis.application.sys.SYS">
				<catagory id="MED_CFG">
					<module id="CFG82">
						<others />
					</module>
				</catagory>
			</app>
		</apps>
		<storage acType="whitelist">
			<others acValue="1111" />
		</storage>
	</accredit>
</role>
