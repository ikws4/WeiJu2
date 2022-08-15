string.split = function(str, regex)
  local res = {}

  for w in str:gmatch("([^" .. regex .. "]+)") do
    res[#res + 1] = w
  end

  return res
end
