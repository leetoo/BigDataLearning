```
LINUX 远程同步工具
rsync [OPTION]... SRC DEST 
rsync [OPTION]... SRC [USER@]host:DEST 
rsync [OPTION]... [USER@]HOST:SRC DEST 
rsync [OPTION]... [USER@]HOST::SRC DEST 
rsync [OPTION]... SRC [USER@]HOST::DEST 
rsync [OPTION]... rsync://[USER@]HOST[:PORT]/SRC [DEST]

1,拷贝本地文件。当SRC和DES路径信息都不包含有单个冒号":"分隔符时就启动这种工作模式。
如：rsync -a /data /backup
2,使用一个远程shell程序(如rsh、ssh)来实现将本地机器的内容拷贝到远程机器。
当DST路径地址包含单个冒号":"分隔符时启动该模式。如：rsync -avz *.c foo:src
3,使用一个远程shell程序(如rsh、ssh)来实现将远程机器的内容拷贝到本地机器。当SRC地址路径包含单个冒号":"分隔符时启动该模式。
如：rsync -avz foo:src/bar /data 
4,从远程rsync服务器中拷贝文件到本地机。当SRC路径信息包含"::"分隔符时启动该模式。
如：rsync -av root@192.168.78.192::www /databack 
5,从本地机器拷贝文件到远程rsync服务器中。当DST路径信息包含"::"分隔符时启动该模式。
如：rsync -av /databack root@192.168.78.192::www 列远程机的文件列表。
这类似于rsync传输，不过只要在命令中省略掉本地机信息即可。如：rsync -v rsync://192.168.78.192/www
```
