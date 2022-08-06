table.extend = function extend(target, source)
	if type(source) ~= "table" then
		return source
	end

	for k, v in pairs(source) do
		target[k] = extend(target[k], v)
	end

	return target
end
