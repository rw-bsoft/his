<?xml version="1.0" encoding="UTF-8"?>
<application id="platform.mpi.MPI" name="主索引管理">
	<catagory id="MPICATA" name="主索引管理">
		<module id="MPI01" name="个人基本信息" script="app.lang.EmbedPanel">
			<properties>
				<p name="url">http://127.0.0.1:8080/mpi/interface.jshtml</p>
				<p name="param"><![CDATA[?uid=system&amp;pass=123&amp;rid=system&amp;ref=MPI01]]></p>
			</properties>
		</module>
		<module id="MPI08" name="卡管理" script="app.lang.EmbedPanel">
			<properties>
				<p name="url">http://127.0.0.1:8080/mpi/interface.jshtml</p>
				<p name="param"><![CDATA[?uid=system&amp;pass=123&amp;rid=system&amp;ref=MPI08]]></p>
			</properties>
		</module>
		<module id="MPI09" name="主索引合并" script="app.lang.EmbedPanel">
			<properties>
				<p name="url">http://127.0.0.1:8080/mpi/interface.jshtml</p>
				<p name="param"><![CDATA[?uid=system&amp;pass=123&amp;rid=system&amp;ref=MPI09]]></p>
			</properties>
		</module>
	</catagory>
</application>