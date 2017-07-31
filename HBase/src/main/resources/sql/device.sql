#设备管理和布控预案
#此表里有单独一行数据用来表示离线告警推送需要比对的对象类型列表，RowKey固定为：offlineWarnRowKey，列名固定为：objTypes
-------------------------
|   name   |   field    |
-------------------------
|  设备ID  |   RowKey   |
-------------------------
|  平台ID  |     p      |
-------------------------
|   备注   |     n      |
-------------------------
|   告警   |     w      |
-------------------------

create 'device',
{NAME => 'device', DATA_BLOCK_ENCODING => 'NONE', BLOOMFILTER => 'ROW', REPLICATION_SCOPE => '0', COMPRESSION =>
'NONE', VERSIONS => '1', MIN_VERSIONS => '0', KEEP_DELETED_CELLS => 'false', BLOCKSIZE => '65536',
 IN_MEMORY => 'true', BLOCKCACHE => 'true'}

 #各个对象类型所绑定设备管理
------------------------
|   name   |   field   |
------------------------
| 对象类型 |   rowKey  |
------------------------
|  平台ID  |   ipcID   |
------------------------
create 'objToDevice',
{NAME => 'objType', DATA_BLOCK_ENCODING => 'NONE', BLOOMFILTER => 'ROW', REPLICATION_SCOPE => '0', COMPRESSION =>
'NONE', VERSIONS => '1', MIN_VERSIONS => '0', KEEP_DELETED_CELLS => 'false', BLOCKSIZE => '65536',
 IN_MEMORY => 'false', BLOCKCACHE => 'false'}
