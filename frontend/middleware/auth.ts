import { useUserStore } from '~/stores/user';
import { useToast } from 'vue-toastification';

export default defineNuxtRouteMiddleware(async (to) => {
    if (process.client) {
        const accessToken = localStorage .getItem('access_token');
        const viewRole = localStorage .getItem('viewRole');
        const userStore = useUserStore();
        const toast = useToast();

        if (!accessToken || !viewRole) {
            toast.error('Vui lòng đăng nhập để tiếp tục.');
            return navigateTo('/');
        }

        if (to.path.includes('/admin') && viewRole !== '1') {
            toast.error('Bạn không có quyền truy cập trang này!'+ viewRole);
            return navigateTo('/');
        }
        if (!userStore.userInfo) {
            const refreshToken = localStorage .getItem('refresh_token');
            if (!refreshToken) {
                return navigateTo('/');
            }

            const url = 'http://localhost:9082/realms/spring/protocol/openid-connect/token';
            const params = new URLSearchParams({
                grant_type: 'refresh_token',
                refresh_token: refreshToken,
                client_id: 'PetHaven',
                client_secret: 'GuFIaAADNfBUpuahqxLvMPWzqt6g8fRL',
            });
            try {
                const response = await fetch(url, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: params.toString()
                });

                if (!response.ok) {
                    return navigateTo('/');
                }

                const data = await response.json();
                localStorage .setItem('access_token', data.access_token);
                localStorage .setItem('refresh_token', data.refresh_token);


                return;
            } catch (error) {
                return navigateTo('/');
            }
        }
    }
});