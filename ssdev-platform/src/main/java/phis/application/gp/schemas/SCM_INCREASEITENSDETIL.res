<?xml version="1.0" encoding="UTF-8"?>
<entity>
	<permissions>
		<p principal="$Others" mode="15">
			<conditions>			 
				<filter action="query">
					['and',['eq',['$','b.LOGOFF'],["s",'1']],['ge',['$','b.STARTDATE'],['$','%server.date.date']],['le',['$','b.ENDDATE'],['$','%server.date.date']],['eq',['$','b.MANAUNIT'],["$",'%user.manageUnit.id']]]
				</filter>			
			</conditions>
		</p>
	</permissions>
</entity>