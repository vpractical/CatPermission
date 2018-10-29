# CatPermission
android权限封装库   
[![](https://jitpack.io/v/vpractical/CatPermission.svg)](https://jitpack.io/#vpractical/CatPermission)

---
#### 1.project的gradle文件中
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

#### 2.module的gradle中
```
	dependencies {
	        implementation 'com.github.vpractical:CatPermission:1.1.0'
	}

```

#### 3.在BaseActivity,BaseFragment中加入:
```
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionCat.onRequestPermissionsResult(this,permissions, grantResults);
    }
```
**注意：第一个参数是当前activity/fragment的对象**
#### 4.使用方式有注解or回调两种
```
    @PermCat(value = PERMISSION_CAMERA)
    public void btn11() {
        if(PermissionCat.has(this,PERMISSION_CAMERA)){
            //TODO
        }else{
            PermissionCat.request("拍照用",this,null,PERMISSION_CAMERA);
        }
    }
```
* 注解：
    * 为方法添加PermCat注解，值为申请的权限中的一个，用于注解冲突过滤
    * has方法判断是否已经授权，参数1：当前类对象。参数2：权限（字符串或字符串数组）
    * request申请授权，参数1：当被禁用并不再询问时，弹窗说明需要权限的原因。
    参数2：当前类对象。参数3：回调对象。参数4：权限。

```
public void btn12() {
        if(PermissionCat.has(this,PERMISSION_CAMERA)){
            //TODO
        }else{
            PermissionCat.request("拍照用", this, new PermissionCallback() {
                @Override
                public void onGranted(String[] permissions, List<String> granted) {
                    if(!granted.isEmpty() && PERMISSION_CAMERA.equals(granted.get(0))){
                        btn12();
                    }
                }

                @Override
                public void onDenied(String[] permissions, List<String> denied) {
                }
            }, PERMISSION_CAMERA);
        }
    }
```
* 回调：
    * 回调方式时不需要为方法添加注解，request方法第三个参数传入PermissionCallback对象。
