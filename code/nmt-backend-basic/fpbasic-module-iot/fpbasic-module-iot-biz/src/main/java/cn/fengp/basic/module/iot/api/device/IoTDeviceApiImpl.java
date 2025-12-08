package cn.fengp.basic.module.iot.api.device;

import cn.fengp.basic.framework.common.enums.RpcConstants;
import cn.fengp.basic.framework.common.pojo.CommonResult;
import cn.fengp.basic.framework.common.util.object.BeanUtils;
import cn.fengp.basic.module.iot.core.biz.IotDeviceCommonApi;
import cn.fengp.basic.module.iot.core.biz.dto.IotDeviceAuthReqDTO;
import cn.fengp.basic.module.iot.core.biz.dto.IotDeviceGetReqDTO;
import cn.fengp.basic.module.iot.core.biz.dto.IotDeviceRespDTO;
import cn.fengp.basic.module.iot.dal.dataobject.device.IotDeviceDO;
import cn.fengp.basic.module.iot.dal.dataobject.product.IotProductDO;
import cn.fengp.basic.module.iot.service.device.IotDeviceService;
import cn.fengp.basic.module.iot.service.product.IotProductService;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import org.springframework.context.annotation.Primary;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static cn.fengp.basic.framework.common.pojo.CommonResult.success;

/**
 * IoT 设备 API 实现类
 *
 * @author haohao
 */
@RestController
@Validated
@Primary // 保证优先匹配，因为 fpbasic-iot-gateway 也有 IotDeviceCommonApi 的实现，并且也可能会被 biz 引入
public class IoTDeviceApiImpl implements IotDeviceCommonApi {

    @Resource
    private IotDeviceService deviceService;
    @Resource
    private IotProductService productService;

    @Override
    @PostMapping(RpcConstants.RPC_API_PREFIX + "/iot/device/auth")
    @PermitAll
    public CommonResult<Boolean> authDevice(@RequestBody IotDeviceAuthReqDTO authReqDTO) {
        return success(deviceService.authDevice(authReqDTO));
    }

    @Override
    @PostMapping(RpcConstants.RPC_API_PREFIX + "/iot/device/get") // 特殊：方便调用，暂时使用 POST，实际更推荐 GET
    @PermitAll
    public CommonResult<IotDeviceRespDTO> getDevice(@RequestBody IotDeviceGetReqDTO getReqDTO) {
        IotDeviceDO device = getReqDTO.getId() != null ? deviceService.getDeviceFromCache(getReqDTO.getId())
                : deviceService.getDeviceFromCache(getReqDTO.getProductKey(), getReqDTO.getDeviceName());
        return success(BeanUtils.toBean(device, IotDeviceRespDTO.class, deviceDTO -> {
            IotProductDO product = productService.getProductFromCache(deviceDTO.getProductId());
            if (product != null) {
                deviceDTO.setCodecType(product.getCodecType());
            }
        }));
    }

}