import { GoogleGenerativeAI } from '@google/generative-ai';
import { useServiceStore } from '~/stores/DichVuStores';
import { useVoucherStore } from '~/stores/VorchersStores';
import { useUserStore } from '~/stores/user';
import {computed, onMounted, onUnmounted, ref} from 'vue';
import { useQuanLyLichHenKhachHang } from '~/stores/QuanLyLichHenKhachHang';
import type { BookingData } from './MauKhachDatDichVu';

export const useAIStore = defineStore('ai', () => {
    const serviceStore = useServiceStore();
    const voucherStore = useVoucherStore();
    const userStore = useUserStore();
    const lichHenStore = useQuanLyLichHenKhachHang();
    const refreshInterval = ref(null);

    const apiKey = 'AIzaSyAngio9lHhhKrSYBeh_RBYxnQvkflv8CXQ';
    const genAI = new GoogleGenerativeAI(apiKey);
    const model = genAI.getGenerativeModel({ model: 'gemini-2.0-flash-exp' });

    const generationConfig = {
        temperature: 1,
        topK: 1,
        topP: 1,
        maxOutputTokens: 2048,
    };

    const services = computed(() =>
        serviceStore.services.filter(service => service.trangthai && service.hien)
    );

    const vouchers = computed(() =>
        voucherStore.ListVoucher.filter(voucher => voucher.trangthai)
    );

    const user = computed(() => userStore.userInfo);

    const getServiceInfo = () =>
        services.value.map(({ id, ten, mota, gia }) => ({ id, ten, mota, gia }));

    const getVoucherInfo = () =>
        vouchers.value.map(({ id, phantramgiam, ngaybatdau, ngayketthuc, mota, trangthai }) =>
            ({ id, phantramgiam, ngaybatdau, ngayketthuc, mota, trangthai }));


    const context = computed(() => {
        const serviceInfo = getServiceInfo();
        const voucherInfo = getVoucherInfo();
        return `
            Bạn là nhân viên chăm sóc khách hàng cho cửa hàng thú cưng PetHaven. Hãy tuân thủ các quy tắc sau:

            1. Trả lời mọi câu hỏi liên quan đến thú cưng, chăm sóc thú cưng, và hoạt động của cửa hàng PetHaven.
            2. Khi được hỏi về dịch vụ hoặc khuyến mãi cụ thể, chỉ trả lời dựa trên thông tin có trong danh sách dưới đây.
            3. Nếu không có thông tin cụ thể trong danh sách, hãy trả lời chung chung về khả năng cung cấp dịch vụ hoặc tư vấn thêm.
            4. Cố gắng trả lời các câu hỏi một cách hài hước và thân thiện, thêm nhiều icon về chó và mèo khi phù hợp.
            5. Trả lời ngắn gọn, súc tích nhưng đầy đủ thông tin.
            6. Khi đề cập đến giá cả hoặc phần trăm giảm giá, hãy sử dụng chính xác số liệu từ danh sách nếu có.
            7. Nếu không chắc chắn về thông tin, hãy đề nghị khách hàng liên hệ trực tiếp với cửa hàng để được tư vấn chi tiết hơn.
            
            Dịch vụ tại cửa hàng hiện có:
            ${serviceInfo.map(service =>
                `- (Tên dịch vụ: ${service.ten}): (Mô tả dịch vụ: ${service.mota}) (Giá dịch vụ: ${service.gia})`
            ).join('\n')}
            
            Chương trình khuyến mãi tại cửa hàng hiện có:
            ${voucherInfo.map(voucher =>
                `- Giảm ${voucher.phantramgiam}%: ${voucher.mota} (Từ ${voucher.ngaybatdau} đến ${voucher.ngayketthuc})`
            ).join('\n')}
        
            Thông tin khách hàng đang đưa ra câu hỏi:
            Tên: ${user.value?.name}
            Vai trò: ${user.value?.roles?.join(', ')}
            Thú cưng:
            ${user.value?.listThuCung?.map((pet, index) =>
                `  ${index + 1}. Tên: ${pet.ten}, Loại: ${pet.loai}, Giống: ${pet.giong}, Tuổi: ${pet.tuoi}`
            ).join('\n') || 'Không có thông tin về thú cưng'}
            
            Đây là lịch sử lịch hẹn của khách hàng:
            ${lichHenStore.appointments.value?.length > 0 ?
                lichHenStore.appointments.value.map((appointment: BookingData, index: number) => `
                    ${index + 1}. Lịch hẹn:
                        - Thời gian: ${new Date(appointment.date).toLocaleString()} ${appointment.idcalichhen.thoigianca}
                        - Dịch vụ: ${appointment.dichvu.tendichvu}
                        - Mô tả dịch vụ: ${appointment.dichvu.mota}
                        - Giá tiền: ${appointment.dichvu.giatien.toLocaleString()} VND
                        - Trạng thái: ${
                            appointment.trangthai === 1 ? 'Đang chờ' :
                            appointment.trangthai === 2 ? 'Đã xác nhận' :
                            appointment.trangthai === 3 ? 'Đã hoàn thành' :
                            appointment.trangthai === 4 ? 'Đã hủy' : 'Không xác định'
                        }
                        - Thú cưng: ${appointment.thucung.ten} (${appointment.thucung.giong}, ${appointment.thucung.tuoi} tuổi, ${appointment.thucung.cannang}kg)
                        ${appointment.thoigianthaydoi ? `- Thời gian thay đổi: ${new Date(appointment.thoigianthaydoi).toLocaleString()}` : ''}
                        ${appointment.thoigianhuy ? `- Thời gian hủy: ${new Date(appointment.thoigianhuy).toLocaleString()}` : ''}
                        - Số lần thay đổi: ${appointment.solanthaydoi}
                    `).join('\n')
                : 'Không có lịch sử lịch hẹn.'
            }
            
            Hãy trả lời câu hỏi của khách hàng một cách linh hoạt, thân thiện và hữu ích nhất có thể.
            Chó 🐕
                🐶 🐕 🐕‍🦺 🦮 🐩 🐾 🦴 🐕‍🦺 🐩 🦮
                🐶 🐕 🐕‍🦺 🦴 🐾 🐩 🦮 🐕 🐾 🦴
                🐶 🐕 🐩 🦮 🐾 🐕‍🦺 🦴 🐶 🐩 🐕‍🦺

            Mèo 🐈
                🐱 🐈 🐈‍⬛ 🐾 🐱 🐈 🐾 🐱 🐈‍⬛ 🐾
                🐱 🐈 🐾 🐱 🐈‍⬛ 🐾 🐱 🐈 🐈‍⬛ 🐾
                🐱 🐈 🐾 🐱 🐈‍⬛ 🐾 🐱 🐈 🐾 🐱.`;
    });

    const chatHistory = ref([
        {
            role: "user",
            parts: [{ text: context.value }],
        },
        {
            role: "model",
            parts: [{ text: "Xin chào! Tôi là nhân viên chăm sóc khách hàng của PetHaven 🐾. Tôi rất vui được hỗ trợ bạn về các dịch vụ và chương trình khuyến mãi của chúng tôi. Hãy hỏi tôi bất cứ điều gì, tôi sẽ trả lời một cách thân thiện và dễ thương nhất có thể! 🐶🐱" }],
        },
    ]);

    const sendMessage = async (prompt: string) => {
        try {
            chatHistory.value.push({ role: "user", parts: [{ text: context.value + "\n\n" + prompt }] });

            const chatSession = model.startChat({
                generationConfig,
                history: chatHistory.value,
            });

            const result = await chatSession.sendMessage(prompt);
            const responseText = result.response.text();

            chatHistory.value.push({ role: "model", parts: [{ text: responseText }] });

            return responseText;
        } catch (error) {
            console.error("Error sending message to AI:", error);
            throw new Error("Xin lỗi, có lỗi xảy ra khi xử lý yêu cầu của bạn. Vui lòng thử lại sau nhé! 🙏");
        }
    };

    const fetchData = async () => {
        await lichHenStore.fetchAppointments();
    };

    onMounted(async () => {
        await fetchData();
        refreshInterval.value = setInterval(fetchData, 60 * 1000);
    });

    return { sendMessage, chatHistory };
});