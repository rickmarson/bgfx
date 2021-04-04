$input v_wpos, v_view, v_normal, v_tangent, v_bitangent, v_texcoord0, v_seg_depth

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

	float depth = clamp((v_seg_depth.x + 1.0) * 0.5, 0.0, 1.0);
	depth = clamp(depth * 100.0, 0.25, 100.0);
	depth = (1.0 / depth) * 0.25;
	depth = floor(clamp(depth, 0.0, 1.0) * 65535.0) - 5.0;

	gl_FragData[0] = texture2D(s_texColor, v_texcoord0);
	gl_FragData[1] = vec4(encodeNormalUint(wnormal), 1.0);
	gl_FragData[2] = vec4(depth / 65535.0, v_seg_depth.y / 65535.0, v_seg_depth.z / 65535.0, 1.0);  // packed values for cpu copy
	gl_FragData[3] = vec4(v_seg_depth.y / 255.0, 0.0, 0.0, 0.0);  // seg debug viz
}
