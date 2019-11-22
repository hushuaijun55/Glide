[同步BLOG请点击](https://blog.csdn.net/hshuaijun55/article/details/103202477)
# 1.背景
一个app中的图片加载类肯定是要封装后再使用的。一方面调用方式统一，另外是稍加调整或更换图片加载库也方便。Android的图片加载类库有很多，目前主流的Glide用起来是相当丝滑的，本文是基于v4以后的版本的介绍。框架里一般的使用方法，及常用方法，各位自行百度google，这里介绍几个我关注的知识点。另外进阶级文章介绍Glide，我推荐链接：https://muyangmin.github.io/glide-docs-cn/doc/download-setup.html 。
# 2.封装成果
## 2.1类源码
```java
public class ImageLoader {

    public static void displayImage(Context context, int res, ImageView imageView) {
        Glide.with(context).load(res).into(imageView);
    }

    public static void displayImage(Context context, int res, int placeholder, ImageView imageView) {
        Glide.with(context).load(res).placeholder(placeholder).into(imageView);
    }

    public static void displayImage(Context context, String url, ImageView imageView) {
        Glide.with(context).load(url).into(imageView);
    }

    public static void displayImage(Context context, String url, ImageView imageView, int width, int height) {
        Glide.with(context).load(url).override(width, height).centerCrop().into(imageView);
    }

    public static void displayImage(Context context, String url, int placeholder, ImageView imageView) {
        Glide.with(context).load(url).placeholder(placeholder).into(imageView);
    }

    public static void displayImage(Context context, String url, int placeholder, int errorRes, ImageView imageView) {
        Glide.with(context).load(url).placeholder(placeholder).error(errorRes).into(imageView);
    }

    public static void displayImage(Context context, String url, int placeholder, final ImageLoadCallback callback) {
        Glide.with(context).asBitmap().load(url).placeholder(placeholder).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                HLogger.d("onResourceReady");
                callback.onResourceReady(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });
    }
}
```
以上封装类是极简单封装，基本上满足大部分应用场景了。Glide框架很多方法的默认配置，已经给我们做了优化。我的整个[demo源码](https://github.com/hushuaijun55/Glide)已经放到GitHub上了。
## 2.2 调用
```java
public void loadImage(View view) {
        ImageLoader.displayImage(MainActivity.this, picUrl, R.mipmap.ic_launcher_round, new ImageLoadCallback() {
            @Override
            public void onResourceReady(Bitmap bitmap) {
                HLogger.d("loadImage");
                imageView.setImageBitmap(bitmap);
            }
        });
//        ImageLoader.displayImage(MainActivity.this, picUrl, imageView, 400, 400);
    }

    public void loadGif(View view) {
        ImageLoader.displayImage(MainActivity.this, gifUrl, imageView);
    }
```
## 2.3使用介绍
相信大家都能看懂这个简单的类的封装，同时各位可以根据自己的需求自行增改符合自己的方法。有几点我提一下：

1.  Context可以是fragment、activity、applicationContext。其实在Glide内，都获取的是applicationContext。

```java
  @GuardedBy("Glide.class")
  @SuppressWarnings("deprecation")
  private static void initializeGlide(
      @NonNull Context context,
      @NonNull GlideBuilder builder,
      @Nullable GeneratedAppGlideModule annotationGeneratedModule) {
      
    Context applicationContext = context.getApplicationContext();
    ...
    }
```
2. override(int width, int height)方法，搭配ImageView ScaleType使用起来真实非常的爽歪歪。如果咱们的应用中有使用到列表展示图片，且图片控件的大小相对固定，我就非常建议使用这个方法，既能加快加载速度，又能一定程度上大大缓解OOM风险。
3. 为什么我没有修改缓存配置。因为默认情况已经做的很好了。v4版本跟v3版本不同的是缓存策略也做了修改。  
+ 默认情况下，Glide 会在开始一个新的图片请求之前检查以下多级的缓存：  
> 1.活动资源 (Active Resources) - 现在是否有另一个 View 正在展示这张图片？
2.内存缓存 (Memory cache) - 该图片是否最近被加载过并仍存在于内存中？
3.资源类型（Resource） - 该图片是否之前曾被解码、转换并写入过磁盘缓存？
4.数据来源 (Data) - 构建这个图片的资源是否之前曾被写入过文件缓存？

前两步检查图片是否在内存中，如果是则直接返回图片。后两步则检查图片是否在磁盘上，以便快速但异步地返回图片。
如果四个步骤都未能找到图片，则Glide会返回到原始资源以取回数据（原始文件，Uri, Url等）。
+ 缓存策略
> DiskCacheStrategy.ALL      ==所有版本都会缓存==
DiskCacheStrategy.NONE      ==所有版本都不缓存==
DiskCacheStrategy.DATA        ==缓存解码之前的原始信息==
DiskCacheStrategy.RESOURCE       ==缓存解码之后的数据信息==
DiskCacheStrategy.AUTOMATIC       ==默认模式==

默认的策略叫做 AUTOMATIC ，它会尝试对本地和远程图片使用最佳的策略。当你加载远程数据（比如，从URL下载）时，AUTOMATIC 策略仅会存储未被你的加载过程修改过(比如，变换，裁剪–译者注)的原始数据，因为下载远程数据相比调整磁盘上已经存在的数据要昂贵得多。对于本地数据，AUTOMATIC 策略则会仅存储变换过的缩略图，因为即使你需要再次生成另一个尺寸或类型的图片，取回原始数据也很容易。

自动模式意思就是对于远程图片，缓存未修改过的原始数据，以便后期基于缓存可以做任意改动。如果加载本地图片，则缓存修改后的结果图片，即使要基于本地原始图片修改，也是相对方便滴。

4. v3版本中的SimpleTarget不建议使用了，现在封装了更多类型的Target。可以用CustomTarget代替以前的SimpleTarget。同时会重写两个方法onResourceReady和onLoadCleared，一般可以先忽略onLoadCleared。这个方法的意义是，如果确定在不需要展示获取到的Bitmap resource时，可以调用Glide.with(fragment).clear(target)；这句代码告知glide，这个target资源或者Bitmap空间可以重用，或回收。

# 3.其他
1. Glide有很多高级用法，比如你的项目工程比较庞大（用户量及业务量），现有的方法策略等不足以满足你的需求，需要基于它做更多的扩展和封装，可以继承自AppGlideModule类，做自己的GlideApp类封装扩展。
2. Glide同时也支持在图片变换、过度动画、缓存配置等等方面根据需求自定义配置，甚至定制使用自己的图片下载ModelLoader。
3. Glide采用引用计数法追踪和重用资源。Glide 中的资源包含很多东西，例如 Bitmap，byte[] 数组， int[] 数组，以及大量的 POJO 。无论什么时候，Glide 都会尝试重用这些资源，以限制你应用中的内存抖动数量。

[同步BLOG请点击](https://blog.csdn.net/hshuaijun55/article/details/103202477)
