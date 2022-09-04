function import(class)
  return luajava.bindClass(class)
end

function object(class, impl_table)
  local ok, proxy = pcall(luajava.createProxy, class, impl_table)

  if ok then
    return proxy
  end

  return luajava.object(class, impl_table)
end
