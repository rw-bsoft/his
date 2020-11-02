<?xml version="1.0" encoding="UTF-8"?>
<entity>
    <permissions>
        <p principal="$Others" mode="15">
            <conditions>
                <filter action="query">
                    ['and',
                        ['eq',['$','b.LOGOFF'],["i",'0']],
                        ['eq',['$','a.LOGOFF'],["i",'0']],
						['like',['$', 'b.manaUtil'],['concat',['substring',['$', '%user.manageUnit.id'],0,9], ['s', '%']]]
                    ]
                </filter>
            </conditions>
        </p>
    </permissions>
</entity>