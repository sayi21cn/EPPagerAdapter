# EPPagerAdapter
This is a project about PagerAdapter for ViewPager when we use ViewPager to display pictures of advertisements.It is automatical that EPPagerAdapter download pictures by their links and save files of pictures in storage. And EPPagerAdapter can only save the valid pictures that delete these pictures which would not be used to display. Then user has no need to concern about managing pictures.  

如果我们经常使用ViewPager在应用中展示切换的焦点图（比如广告图等等），那么EPPagerAdapter能帮我们较好的实现这件事。只需要设置图片的下载链接，它能自动下载图片文件并保存在本地，达到往后复用的目的。用户可以不必关心如何去管理这些焦点图片。（本地只会保存最新且有效的图片文件，旧且无效图片会自动被删除，因此也不会占用不合理的存储空间。）



