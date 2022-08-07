# WeiJu2 (Working In Progress)

Scriptable Xposed Module


# Features

- Lua Scripting
- Provide a scripts/package [repository](https://github.com/ikws4/WeiJu2-Scripts)

# Examples

### How to write a hook?

```lua
-- You can import any java class as long as
-- it available in that app's classloader
local Toast = import("android.widget.Toast")
local Activity = import("android.app.Activity")
local Bundle = import("android.os.Bundle")
local StringBuilder = import("java.lang.StringBuilder")

hook {
  class = Activity,
  returns = void,
  method = "onCreate",
  params = {
    Bundle
  },
  after = function(this, params)
    -- This will call the `StringBuilder(CharSequence seq)` constructor
    -- to instantiate a StringBuilder object
    local sb = StringBuilder("Hello, ")
    sb:append("WeiJu2")
  
    Toast:makeText(this, sb:toString(), Toast.LENGTH_SHORT):show()
    --              ^
    -- Note: `this` is the Activity instance
  end,
}
```

### How to modify class fields?

```lua
-- With this `import` function you can bind any java class, and access all the fields that defined
-- in that class. No more `XposedHelper.setStaticField(Build.class, "DEVICE", "coral")` much cleaner!
local Build = import("android.os.Build")

Build.DEVICE = "coral"
Build.PRODUCT = "coral"
Build.MODEL = "Google Pixel 4XL"
Build.BRAND = "google"
Build.MANUFACTURER = "google"
Build.VERSION.RELEASE = "13"
```



# Screenshots

<img src="https://user-images.githubusercontent.com/47056144/183251553-9dce66f7-953c-45b9-b741-0ae8e0b567af.png" />
