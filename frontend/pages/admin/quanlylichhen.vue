<template>
  <div class="container">
    <div v-if="isLoading" class="text-center my-4">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
    </div>
    <div v-else class="card">
      <div class="card-header text-white d-flex justify-content-between align-items-center">
        <h5 class="mb-0 text fs-4">{{ t('allAppointments') }}</h5>
        <button @click="fetchHoaDon" class="btn btn-sm btn-outline-success">
          {{ t('refresh') }}
        </button>
      </div>
      <div class="card-body">
        <div class="mb-3">
          <input v-model="searchQuery" @input="handleSearch" type="text" class="form-control"
            :placeholder="t('searchAppointmentPlaceholder')">
        </div>
        <table class="table">
          <thead>
            <tr>
              <th>{{ t('appointmentId') }}</th>
              <th>{{ t('customerEmail') }}</th>
              <th>{{ t('petName') }}</th>
              <th>{{ t('time') }}</th>
              <th>{{ t('appointmentDate') }}</th>
              <th>{{ t('status') }}</th>
              <th>{{ t('actions') }}</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="lichhen in paginatedHoaDon" :key="lichhen.id">
              <td>{{ lichhen.id }}</td>
              <td>{{ lichhen.emailNguoiDat }}</td>
              <td>{{ lichhen.thucung.ten }}</td>
              <td>{{ lichhen.idcalichhen.thoigianca }}</td>
              <td>{{ formatDate(lichhen.date) }}</td>
              <td><span class="badge bg-success">{{ getTrangThai(lichhen.trangthai) }}</span></td>
              <td>
                <div class="row" v-if="lichhen.trangthai!=2">
                  <div class="col" >
                    <button type="button" class="btn btn-sm btn-outline-danger" @click="thayDoiThoiGian(lichhen)">
                      {{ t('changeTime') }}
                    </button>
                  </div>
                  <div class="col">
                    <button type="button" class="btn btn-sm btn-outline-success" @click="thayDoiTrangThai(lichhen)">
                      {{ t('changeStatus') }}
                    </button>
                  </div>
                  <div class="col">
                    <button type="button" class="btn btn-sm btn-outline-info" @click="viewHoaDon(lichhen.id)">
                      {{ t('viewDetails') }}
                    </button>
                  </div>
                  <div class="col">
                    <button v-if="lichhen.trangthai !== 7"
                        @click="DoiDichVu(lichhen.id)"
                        class="btn btn-sm btn-outline-primary">
                      {{t('change_service')}}
                    </button>
                  </div>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
        <div class="d-flex justify-content-center align-items-center mt-4">
          <div class="d-flex align-items-center">
            <button @click="changePage(currentPage - 1)" :disabled="currentPage === 1" class="custom-button me-3">
              {{ t('previous') }}
            </button>
            <span class="fs-5 mx-3">{{ t('page') }} {{ currentPage }} / {{ totalPages }}</span>
            <button @click="changePage(currentPage + 1)" :disabled="currentPage === totalPages" class="custom-button ms-3">
              {{ t('next') }}
            </button>
          </div>
        </div>
      </div>
    </div>
    <div class="card">
      <div class="card-header text-white d-flex justify-content-between align-items-center">
        <p class="col text fs-4">
          {{ t('listOfInvoicesAfterServiceChange') }}
        </p>
        <div class="col   ">
          <button type="button" class="btn btn-sm btn-outline-success" @click="fetchHoaDonChuaThanhToan">{{ t('refresh') }}</button>
        </div>
      </div>
      <div class="card-body">
        <table class="table">
          <thead>
          <tr>
            <th>{{ t('appointmentId') }}</th>
            <th>{{ t('customerEmail') }}</th>
            <th>{{ t('petName') }}</th>
            <th>{{ t('time') }}</th>
            <th>{{ t('appointmentDate') }}</th>
            <th>{{ t('status') }}</th>
            <th>{{ t('note') }}</th>
            <th>{{ t('actions') }}</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="hoadon in paginatedHoaDonChuaThanhToan" :key="hoadon.id">
            <td>{{ hoadon.idhoadon.idlichhen.id }}</td>
            <td>{{ hoadon.nguoithanhtoan }}</td>
            <td>{{ hoadon.idhoadon.idlichhen.thucung.ten }}</td>
            <td>{{ hoadon.idhoadon.idlichhen.idcalichhen.thoigianca }}</td>
            <td>{{ hoadon.idhoadon.idlichhen.date }}</td>
            <td><span class="badge bg-success">{{getTrangThai(hoadon.idhoadon.idlichhen.trangthai) }}</span></td>
            <td>{{hoadon.ghichu?hoadon.ghichu:'-'}}</td>
            <td>
              <div class="row">
                <div class="col">
                    <button type="button" class="btn btn-sm btn-outline-primary m-1" @click="thanhToanOffice(hoadon.id)" v-if="!hoadon.trangthaithanhtoan && hoadon.trangthai===2">Thanh toán</button>
                </div>
              </div>
            </td>

          </tr>
          </tbody>
        </table>
        <div class="d-flex justify-content-center align-items-center mt-4">
          <div class="d-flex align-items-center">
            <button @click="changePageHoaDon(currentPageHoaDon - 1)" :disabled="currentPageHoaDon === 1" class="custom-button me-3">
              {{ t('previous') }}
            </button>
            <span class="fs-5 mx-3">{{ t('page') }} {{ currentPageHoaDon }} / {{ totalPagesHoaDon }}</span>
            <button @click="changePageHoaDon(currentPageHoaDon + 1)" :disabled="currentPageHoaDon === totalPagesHoaDon" class="custom-button ms-3">
              {{ t('next') }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, onUnmounted, computed } from 'vue';
import { useQuanLyLichHenAdminStore } from '~/stores/QuanLyLichHenAdmin';
import { useToast } from 'vue-toastification';
import { useI18n } from 'vue-i18n';
import type { Lichhen } from "~/models/LichSuDatLich";
import {useDoiDichVuStores} from "~/stores/DoiDichVuStores";
import type {HoaDonDoiDichVu} from "~/models/HoaDonDoiDV";
import Swal from "sweetalert2";

const { t } = useI18n();
const useQuanLyAdmin = useQuanLyLichHenAdminStore();
const lichhen = ref<Lichhen[]>([]);
const filteredHoaDon = ref<Lichhen[]>([]);
const toast = useToast();
const isLoading = ref(true);
let refreshInterval: NodeJS.Timeout;
const itemsPerPage = 5;
const currentPage = ref(1);
const searchQuery = ref('');
const listAllHoaDonThayDoiDichVu = ref<HoaDonDoiDichVu[]>([]);
const useDoiDichVu = useDoiDichVuStores();

// Debounce function
const debounce = (fn: Function, delay: number) => {
  let timeoutId: NodeJS.Timeout;
  return (...args: any[]) => {
    clearTimeout(timeoutId);
    timeoutId = setTimeout(() => fn(...args), delay);
  };
};

onMounted(async () => {
  try {
    isLoading.value = true;
    await Promise.all([
      fetchHoaDon(),
      useDoiDichVu.fetchAllHoaDonDoiDichVu()
    ]);
    listAllHoaDonThayDoiDichVu.value = useDoiDichVu.listAllHoaDonDoiDichVu;
    refreshInterval = setInterval(fetchHoaDon, 5 * 60 * 1000);
  } catch (error) {
    console.error('Error loading initial data:', error);
    toast.error(t('errorLoadingData'));
  } finally {
    isLoading.value = false;
  }
});

async function fetchHoaDonChuaThanhToan() {
  try {
    await useDoiDichVu.fetchAllHoaDonDoiDichVu();
    listAllHoaDonThayDoiDichVu.value = useDoiDichVu.listAllHoaDonDoiDichVu;
  } catch (error) {
    console.error('Error fetching unpaid invoices:', error);
    toast.error(t('errorFetchingUnpaidInvoices'));
  }
}

const handleSearch = debounce(() => {
  filteredHoaDon.value = lichhen.value.filter(hoaDon =>
    hoaDon.emailNguoiDat.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
    hoaDon.thucung.ten.toLowerCase().includes(searchQuery.value.toLowerCase())||
    hoaDon.id.toString().includes(searchQuery.value)
  );
  currentPage.value = 1;
}, 300);

const totalPages = computed(() => Math.ceil(filteredHoaDon.value.length / itemsPerPage));

const paginatedHoaDon = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return filteredHoaDon.value.slice(start, end);
});

const changePage = (page: number) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page;
  }
};

onUnmounted(() => {
  fetchHoaDon();
  fetchHoaDonChuaThanhToan();
  if (refreshInterval) clearInterval(refreshInterval);
});

const fetchHoaDon = async () => {
  try {
    await useQuanLyAdmin.fetchHoaDonKhachHangs();
    lichhen.value = useQuanLyAdmin.hoaDonKhachHangs;
    filteredHoaDon.value = lichhen.value;
  } catch (error) {
    console.error('Lỗi khi tải dữ liệu:', error);
    toast.error(t('loadDataError'));
  }
};
const currentPageHoaDon = ref(1);
const itemsPerPageHoaDon = 5;

const totalPagesHoaDon = computed(() => Math.ceil(listAllHoaDonThayDoiDichVu.value.length / itemsPerPageHoaDon));

const paginatedHoaDonChuaThanhToan = computed(() => {
  const start = (currentPageHoaDon.value - 1) * itemsPerPageHoaDon;
  const end = start + itemsPerPageHoaDon;
  return listAllHoaDonThayDoiDichVu.value.slice(start, end);
});

const changePageHoaDon = (page: number) => {
  if (page >= 1 && page <= totalPagesHoaDon.value) {
    currentPageHoaDon.value = page;
  }
};

const getBadgeClass = (status: number): string => {
  const statusClasses: { [key: number]: string } = {
    0: 'bg-success',
    1: 'bg-warning',
    2: 'bg-danger',
    3: 'bg-info',
    4: 'bg-primary',
    5: 'bg-secondary',
    6: 'bg-success',
    7: 'bg-danger',
    8: 'bg-info'
  };
  return statusClasses[status] || 'bg-secondary';
};
const formatDate = (dateString: string): string => {
  const date = new Date(dateString);
  return date.toLocaleDateString('vi-VN');
};

const formatCurrency = (amount: number): string => {
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'USD' }).format(amount);
};

const getTrangThai = (status: number): string => {
  const trangThaiMap: { [key: number]: string } = {
    0: t('statusSuccess'),
    1: t('statusChanged'),
    2: t('statusCancelled'),
    3: t('statusPendingPayment'),
    4: t('statusPendingConfirmation'),
    5: t('statusEmpty'),
    6: t('statusPaymentSuccess'),
    7: t('statusRefunded'),
    8: t('statusPendingService')
  };
  return trangThaiMap[status] || t('statusUnknown');
};
const viewHoaDon = (id: number) => {
  navigateTo(`/nhanvien/chitiethoadon/${id}`);
};

const token = localStorage.getItem('access_token');

const thayDoiTrangThai = async (lichHen: Lichhen) => {
  try {
    const response = await fetch(`http://localhost:8080/api/lich-hen/updateTrangThai/${lichHen.id}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`,
      },
    });
    if (response.ok) {
      console.log("ok");
    } else {
      console.error("Failed to update status", await response.text());
    }
  } catch (error) {
    console.error("Error updating status:", error);
  }
};


const thayDoiThoiGian = (lichHen: Lichhen) => {
  navigateTo(`/admin/thaydoithoigian/${lichHen.id}`);
}

const DoiDichVu = (id : number) => {
  navigateTo(`/thay-doi-lich/${id}`)
}


async function thanhToanOffice(id : number) {
  try {
    const result = await Swal.fire({
      title: t('are_you_sure_you_want_to_change'),
      text: `Bạn có chắc chắn muốn thanh toán hóa đơn đã đối dịch vụ?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#3085d6',
      cancelButtonColor: '#d33',
      confirmButtonText: t('yes'),
      cancelButtonText: t('no')
    });
    if(result.isConfirmed) {
      await useDoiDichVu.thanhToanDichVuKhiSoTienOffice(id);
      toast.success(t('payment_office_success'));
      await fetchHoaDonChuaThanhToan();
    }
  } catch (error) {
    toast.error(t('payment_office_error'));
  }
}
</script>

<style scoped>

</style>