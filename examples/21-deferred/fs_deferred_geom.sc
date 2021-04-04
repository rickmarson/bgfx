$input v_wpos, v_view, v_normal, v_tangent, v_bitangent, v_texcoord0, v_seg_inst

/*
 * Copyright 2011-2021 Branimir Karadzic. All rights reserved.
 * License: https://github.com/bkaradzic/bgfx#license-bsd-2-clause
 */

#include "../common/common.sh"

SAMPLER2D(s_texColor,  0);
SAMPLER2D(s_texNormal, 1);

void main()
{
	vec3 normal;
	normal.xy = texture2D(s_texNormal, v_texcoord0).xy * 2.0 - 1.0;
	normal.z  = sqrt(1.0 - dot(normal.xy, normal.xy) );

	mat3 tbn = mat3(
				normalize(v_tangent),
				normalize(v_bitangent),
				normalize(v_normal)
				);

	normal = normalize(mul(tbn, normal) );

	vec3 wnormal = normalize(mul(u_invView, vec4(normal, 0.0) ).xyz);

	float z_n = 0.1;
	float z_f = 100.0;
	float depth = (2.0 * z_n) / (z_f + z_n - gl_FragCoord.z * (z_f - z_n));
	depth = (1.0 / depth) * 0.1;  // inverse depth
	depth = clamp(depth - 7.63e-5, 0.0, 1.0);
	
	gl_FragData[0] = texture2D(s_texColor, v_texcoord0);
	gl_FragData[1] = vec4(encodeNormalUint(wnormal), 1.0);
	gl_FragData[2] = vec4(depth, v_seg_inst.x / 65535.0, v_seg_inst.y / 65535.0, 1.0);  // packed values for cpu copy
	gl_FragData[3] = vec4(depth, depth, depth, 1.0);  // depth debug viz
	gl_FragData[4] = vec4(0.0, v_seg_inst.x * 5.0 / 255.0, 0.0, 1.0);  // seg debug viz
}
