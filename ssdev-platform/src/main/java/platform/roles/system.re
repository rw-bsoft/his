<?xml version="1.0" encoding="UTF-8"?>

<role id="platform.system" name="系统管理员" parent="platform.base" pageCount="4">
    <home>
        <module id="platform.index.index/REG3_01"/>
        <module id="platform.index.index/SYS_02"/>
    </home>
	<accredit>
		<apps>
			<app id="platform.index.index">
				<others />
			</app>
			<app id="platform.dataset.DATASET">
				<others />
			</app>
			<app id="platform.monitor.MONITOR">
				<others />
			</app>
			<app id="platform.mpi.MPI">
				<others />
			</app>
			<app id="platform.reg.REG">
				<others />
			</app>
			<app id="platform.security.SECURITY">
				<others />
			</app>
			<app id="platform.transport.TRANSPORT">
				<others />
			</app>
		</apps>
		<storage acType="whitelist">
			<others acValue="1111" />
		</storage>
	</accredit>
</role>
