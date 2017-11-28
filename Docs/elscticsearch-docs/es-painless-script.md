```
PUT hockey/player/_bulk?refresh 
{"index":{"_id":1}} 
{"first":"johnny","last":"gaudreau","goals":[9,27,1],"assists":[17,46,0],"gp":[26,82,1],"born":"1993/08/13"} 
{"index":{"_id":2}} 
{"first":"sean","last":"monohan","goals":[7,54,26],"assists":[11,26,13],"gp":[26,82,82],"born":"1994/10/12"} 
{"index":{"_id":3}} 
{"first":"jiri","last":"hudler","goals":[5,34,36],"assists":[11,62,42],"gp":[24,80,79],"born":"1984/01/04"} 
{"index":{"_id":4}} 
{"first":"micheal","last":"frolik","goals":[4,6,15],"assists":[8,23,15],"gp":[26,82,82],"born":"1988/02/17"} 
{"index":{"_id":5}} 
{"first":"sam","last":"bennett","goals":[5,0,0],"assists":[8,1,0],"gp":[26,1,0],"born":"1996/06/20"} 
{"index":{"_id":6}} 
{"first":"dennis","last":"wideman","goals":[0,26,15],"assists":[11,30,24],"gp":[26,81,82],"born":"1983/03/20"} 
{"index":{"_id":7}} 
{"first":"david","last":"jones","goals":[7,19,5],"assists":[3,17,4],"gp":[26,45,34],"born":"1984/08/10"} 
{"index":{"_id":8}} 
{"first":"tj","last":"brodie","goals":[2,14,7],"assists":[8,42,30],"gp":[26,82,82],"born":"1990/06/07"} 
{"index":{"_id":39}} 
{"first":"mark","last":"giordano","goals":[6,30,15],"assists":[3,30,24],"gp":[26,60,63],"born":"1983/10/03"} 
{"index":{"_id":10}} 
{"first":"mikael","last":"backlund","goals":[3,15,13],"assists":[6,24,18],"gp":[26,82,82],"born":"1989/03/17"} 
{"index":{"_id":11}} 
{"first":"joe","last":"colborne","goals":[3,18,13],"assists":[6,20,24],"gp":[26,67,82],"born":"1990/01/30"}

GET /hockey/_search
{
  "query": {
    "function_score": {
      "script_score": {
        "script": {
          "lang": "painless",
          "inline": "int total = 0; for(int i = 0; i < doc['goals'].length; ++i) {total += doc['goals'][i];} return total;"
        }
      }
    }
  }
}

返回结果：
{
  "took": 5,
  "timed_out": false,
  "_shards": {
    "total": 5,
    "successful": 5,
    "failed": 0
  },
  "hits": {
    "total": 11,
    "max_score": 87,
    "hits": [
      {
        "_index": "hockey",
        "_type": "player",
        "_id": "2",
        "_score": 87,
        "_source": {
          "first": "sean",
          "last": "monohan",
          "goals": [
            7,
            54,
            26
          ],
          "assists": [
            11,
            26,
            13
          ],
          "gp": [
            26,
            82,
            82
          ],
          "born": "1994/10/12"
        }
      },
      {
        "_index": "hockey",
        "_type": "player",
        "_id": "3",
        "_score": 75,
        "_source": {
          "first": "jiri",
          "last": "hudler",
          "goals": [
            5,
            34,
            36
          ],
          "assists": [
            11,
            62,
            42
          ],
          "gp": [
            24,
            80,
            79
          ],
          "born": "1984/01/04"
        }
      },
.........


GET /hockey/_search
{
  "query": {
    "match_all": {}
  },
  "sort": {
    "_script":{
      "type": "string",
      "order": "asc",
      "script": {
        "lang": "painless",
        "inline": "doc['first.keyword'].value + '' + doc['last.keyword'].value"
      }
    }
  }
}
"hits": {
"total": 11,
"max_score": null,
"hits": [
{
"_index": "hockey",
"_type": "player",
"_id": "7",
"_score": null,
"_source": {
  "first": "david",
  "last": "jones",
  "goals": [
    7,
    19,
    5
  ],
  "assists": [
    3,
    17,
    4
  ],
  "gp": [
    26,
    45,
    34
  ],
  "born": "1984/08/10"
},
"sort": [
  "davidjones"
]
},
.......
这里需要注意几点：

这里都是_search操作，多个操作之间会形成管道，
既query::match_all的输出会作为script_fields或者sort的输入。
_search操作中所有的返回值，都可以通过一个map类型变量doc获取。
和所有其他脚本语言一样，用[]获取map中的值。这里要强调的是，
doc只可以在_search中访问到。在下一节的例子中，你将看到，使用的是ctx。
_search操作是不会改变document的值的，即便是script_fields，
你只能在当次查询是能看到script输出的值。
doc['first.keyword']这样的写法是因为doc[]返回有可能是分词之后的value，
所以你想要某个field的完整值时，请使用keyword

跟新数据：

POST hockey/player/1/_update 
{
  "script": {
    "lang": "painless",
    "inline": "ctx._source.last = params.last; ctx._source.nick = params.nick", 
    "params": {
      "last": "gaudreau",
      "nick": "hockey"
    }
  }
}

POST hockey/player/1/_update 
{
  "script": {
    "lang": "painless",
    "inline": "ctx._source.born = params.born", 
    "params": {
      "born": "1993/08/16"
    }
  }
}


正则表达式
日期与常规值有所不同。 这是一个返回每个玩家诞生年份的例子：
GET hockey/_search
{
  "script_fields": {
    "birth_year": {
      "script": {
        "inline": "doc.born.date.year"
      }
    }
  }
}
这里的关键是不能直接编入 doc.born，就像您正常的字段，
你必须调用 doc.born.date 来获取一个 ReadableDateTime 。 
从那里可以调用 getYear 和 getDayOfWeek 等方法。 在上面的例子中，getYear() 的一个快捷方式。 
如果日期字段是列表，那么日期将始终返回第一个日期。
 要访问所有日期，请使用 dates 而不是 date。


```
