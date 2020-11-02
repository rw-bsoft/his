<?xml version="1.0" encoding="UTF-8"?>
<entity>
	<permissions>
		<p principal="$Others" mode="15">
			<conditions>			 
				<filter action="query">
					['and',
						['like',['$','a.operatorUnit'], ['concat',['$','%user.manageUnit.id'],['s','%']]]
					<!--  ['and',	
						['le',['$','a.beginDate'],['todate',['$','%server.date.today'],['s','yyyy-mm-dd']]],
						['ge',['$','a.endDate'],['todate',['$','%server.date.today'],['s','yyyy-mm-dd']]]] -->
					]	
				</filter>			
			</conditions>
		</p>
	</permissions>
</entity>