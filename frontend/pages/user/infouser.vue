<script setup lang="ts">
import {useUserStore} from '~/stores/user';
import {computed} from "vue";
import ThemThuCung from "~/pages/user/themthucung.vue"
import CapNhatThuCung from "~/components/CapNhatThuCung.vue"
import type {ThuCung} from "~/models/ChiTietDanhGia";

const userStore = useUserStore();

const userInfo = computed(() => userStore.userInfo);
definePageMeta({
  middleware: ['auth']
})

function login1() {
  if (process.client) {
    window.location.href = 'http://localhost:8080/oauth2/authorization/keycloak';
  }
}

if (userInfo.value === null) {
  login1();
}
const refreshAccessToken = async () => {
  const refreshToken = localStorage.getItem('refresh_token');
  if (!refreshToken) {
    console.error('No refresh token available');
    userStore.setLoggedIn(false);
    return;
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
      const errorData = await response.json();
      console.error(`Error refreshing token: ${errorData.error_description}`);
    }

    const data = await response.json();
    localStorage.setItem('access_token', data.access_token);
    localStorage.setItem('refresh_token', data.refresh_token);
    console.log('New access token:', data.access_token);
    return data.access_token;
  } catch (error) {
    localStorage.setItem('viewRole', '0');
    console.error('Error refreshing token:', error);
    return null;
  }
};

const fetchUserInfo = async (accessToken: string) => {
  const url = 'http://localhost:8080/api/user';
  try {
    const response = await fetch(url, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${accessToken}`,
        'Content-Type': 'application/json'
      }
    });

    if (response.status === 401) {
      console.log('Access token expired, refreshing token...');
      const newAccessToken = await refreshAccessToken();
      if (newAccessToken) {
        return fetchUserInfo(newAccessToken);
      } else {
        console.error('Unable to refresh access token');
        userStore.setLoggedIn(false);
        return;
      }
    }

    if (!response.ok) {
      const errorData = await response.json();
      console.error(`Error fetching user info: ${errorData.error_description}`);
      return;
    }

    const userInfoData = await response.json();
    console.log('User Info:', userInfoData);

    const roles = userInfoData.roles || [];
    userStore.setUserInfo({
      name: userInfoData.username || '',
      role: roles || '',
      listThuCung: userInfoData.listThuCung || ''
    });

    userStore.setLoggedIn(true);

    if (userInfoData.username) {
      console.log('User info refreshed successfully');
    }
  } catch (error) {
    localStorage.setItem('viewRole', '0');
    console.error('Error fetching user info:', error);
    userStore.setLoggedIn(false);
  }
};

function restUser() {
  const accessToken = localStorage.getItem('access_token');
  if (!accessToken) {
    console.error('No access token available');
    return;
  }else {
    fetchUserInfo(accessToken);
    return navigateTo('/user/infouser');
  }
}

onMounted(() => {
  const accessToken = localStorage.getItem('access_token');
  if (accessToken) {
    fetchUserInfo(accessToken);
  }
})

function themThuCung() {
  return navigateTo('/user/themthucung');
}

function capNhatThuCung(thuCung : ThuCung) {
  return navigateTo(`/user/capnhatthucung/${thuCung.id}`);
}

</script>

<template>
  <div class="container p-2">
    <div class="card">
      <div class="card-body">
        <div class="form-group">
          <div class="row">
            <div class="col-4">
              <img class="card-img-top" src="../../assets/image/cat3.jpg" alt="">
            </div>
            <div class="col-8">
              <div
                  v-if="Array.isArray(userInfo.role) && userInfo.role.includes('admin') || userInfo.role.includes('manager')">
                <div class="p-4 text fs-4">
                  <div v-if="userInfo.role.includes('admin')">
                    Bạn đang đăng nhập với tài khoản có quyền Admin
                  </div>
                  <div v-else-if="userInfo.role.includes('manager')">
                    Bạn đang đăng nhập với tài khoản có quyền nhân viên
                  </div>
                  <div v-else>
                    Chào mừng khách hàng cảm ơn bạn đã sử dụng dịch vụ !
                  </div>
                </div>
              </div>
              <label for="">Tên người dùng</label>
              <input type="text" class="form-control" name="" id="name" aria-describedby="helpId" placeholder=""
                     v-model="userInfo.name">
            </div>
          </div>
          <div class="container p-4">
            <div class="row">
              <div class="col-2">
                <button type="button" class="custom-button" @click="themThuCung">Thêm thú cưng</button>
              </div>
            </div>
            <div v-if="userInfo?.listThuCung == 0">
              Bạn chưa lưu thông tin thú cưng nào !
            </div>
            <div v-else>
              <div class="text fs-4">
               <div class="row">
                 <div class="col">
                   Danh sách thú cưng của của chủ nhân :
                 </div>
                 <div class="col">
                   <button type="button" class="btn btn-sm btn-warning m-1" @click="restUser">Làm mới</button>
                 </div>
               </div>
              </div>
              <div v-if="userInfo.listThuCung.length > 0" v-for="thuCung in userInfo?.listThuCung"
                   :key="userInfo?.listThuCung.id" class="card m-4">
                <div class="card-body">
                  <table class="table">
                    <thead>
                    <tr>
                      <th>Tên</th>
                      <th>Cân nặng</th>
                      <th>Giống</th>
                      <th>Giới tính</th>
                      <th>Loại thú cưng</th>
                      <th>Tuổi</th>
                      <th>Thao tác</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                      <td>{{ thuCung.ten }}</td>
                      <td>{{ thuCung.cannang }}</td>
                      <td>{{ thuCung.giong }}</td>
                      <td>{{thuCung.gioitinh?'Cái':'Đựa'}}</td>
                      <td>{{thuCung.cophaimeokhong?'Mèo':'Chó'}}</td>
                      <td>{{ thuCung.tuoi }}</td>

                      <td>
                        <button type="button" class="btn btn-sm btn-warning m-1" @click="capNhatThuCung=(thuCung)">Cập nhật thú cưng</button>
                      </td>
                    </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped></style>