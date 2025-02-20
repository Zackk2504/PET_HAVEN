<template>
  <div class="container card mx-auto bg-light p-4" style="border-radius: 25px">
    <h2 class="text font-bold mb-6 text-center text-gray-800">{{ t('statistics') }}</h2>
    <div class="card shadow-md rounded-lg overflow-hidden p-4">
      <div class="p-6 space-y-6">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div class="row p-4">
            <div class="col">
              <label class="block text-sm font-medium text-gray-700 mb-2">{{ t('selectDateRange') }}</label>
              <date-picker v-model="dateRange" range @change="updateDateRange" value-type="format" format="YYYY-MM-DD"
                :placeholder="[t('startDate'), t('endDate')]"
                class="w-full border rounded-md p-2 focus:ring-2 focus:ring-blue-500 focus:border-blue-500" />
            </div>
            <div class="col">
              <label for="statisticType" class="block text-sm font-medium text-gray-700 mb-2">{{
                t('statisticType')
              }}</label>
              <select v-model="statisticType" id="statisticType"
                class="w-full border rounded-md p-2 focus:ring-2 focus:ring-blue-500 focus:border-blue-500">
                <option value="day">{{ t('byDay') }}</option>
                <option value="month">{{ t('byMonth') }}</option>
                <option value="year">{{ t('byYear') }}</option>
              </select>
            </div>
          </div>
        </div>
        <div class="flex justify-between items-center">
          <p v-if="dateRange[0] && dateRange[1]" class="text-sm text-gray-600">
            {{ t('selectedTimeRange', { start: formatDisplayDate(dateRange[0]), end: formatDisplayDate(dateRange[1]) }) }}
            <button @click="fetchData" class="custom-button">
              {{ t('getData') }}
            </button>
          </p>

        </div>
      </div>
    </div>
    <div>
      <div class="card">
        <div class="card-body">
          <div v-if="loading" class="mt-8 text-center">
            <p class="text-gray-600">{{ t('loading') }}</p>
          </div>

          <div v-else-if="error" class="mt-8 text-center text-red-500">
            <p>{{ error }}</p>
          </div>
          <div v-else-if="hasData" class="mt-8">
            <h3 class="text-xl font-semibold mb-4 text-gray-800">{{ t('statisticsData') }}</h3>

            <!-- ECharts Component -->
            <div class="bg-white shadow-lg rounded-lg overflow-hidden p-4 mb-8">
              <v-chart class="chart" :option="chartOption" autoresize />
            </div>

            <div class="bg-white shadow-lg rounded-lg overflow-hidden">
              <table class="min-w-full divide-y divide-gray-200 m-4 p-4">
                <thead class="bg-gray-50">
                  <tr>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      {{ t('date') }}
                    </th>
                    <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      {{ t('amount') }}
                    </th>
                  </tr>
                </thead>
                <tbody class="bg-white divide-y divide-gray-200">
                  <tr v-for="(item, index) in store.thongKeItems" :key="index"
                    class="hover:bg-gray-50 transition-colors duration-200">
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {{ formatDisplayDate(item.date.toString()) }}
                    </td>
                    <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ item.amount }} USD</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
          <div v-else class="mt-8 text-center">
            <p class="text-gray-600">{{ t('noData') }}</p>
          </div>
        </div>
      </div>
      <div class="card">
        <div class="card-body">
          <h3 class="text-xl font-semibold mb-4 text-gray-800">{{ t('serviceUsage') }}</h3>
          <div v-if="hasServiceData" class="bg-white shadow-lg rounded-lg overflow-hidden p-4 mb-8">
            <v-chart class="chart" :option="serviceChartOption" autoresize />
          </div>
          <div v-else class="mt-8 text-center">
            <p class="text-gray-600">{{ t('noServiceData') }}</p>
          </div>
        </div>
      </div>
      <div class="card">
        <div class="card-header text fs-4">
          {{ t('serviceRevenue') }}
        </div>
        <div class="card-body">
          <table class="table">
            <thead>
            <tr>
              <th>{{t('serviceName')}}</th>
              <th>{{t('number_of_uses')}}</th>
              <th>{{t('revenue')}}</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="dt in doanhThuTheoDichVu" :key="dt[0]">
              <td>{{ dt[0] }}</td>
              <td>{{ dt[1] }}</td>
              <td>{{ dt[2] }} USD</td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>
      <div>

      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, computed, onUnmounted } from 'vue';
import { useThongKeStore } from '~/stores/ThongKeStores';
import DatePicker from 'vue-datepicker-next';
import 'vue-datepicker-next/index.css';
import { useToast } from 'vue-toastification';
import VChart from 'vue-echarts';
import { use } from 'echarts/core';
import { CanvasRenderer } from 'echarts/renderers';
import { LineChart, PieChart } from 'echarts/charts';
import { GridComponent, TooltipComponent, LegendComponent } from 'echarts/components';
import { useI18n } from 'vue-i18n';

// Register ECharts components
use([CanvasRenderer, LineChart, PieChart, GridComponent, TooltipComponent, LegendComponent]);

const { t } = useI18n();
const toast = useToast();
const store = useThongKeStore();
const dateRange = ref<[string | null, string | null]>([null, null]);
const statisticType = ref('day');
const loading = ref(false);
const error = ref<string | null>(null);


const serviceRevenueData = ref([]);
const doanhThuTheoDichVu = ref(null);

const hasData = computed(() => store.thongKeItems.length > 0);
const hasServiceData = computed(() => serviceRevenueData.value.length > 0);

const chartOption = computed(() => ({
  tooltip: {
    trigger: 'axis',
    formatter: function (params) {
      const date = formatDisplayDate(params[0].name);
      const value = params[0].value;
      return `${date}: ${value} USD`;
    }
  },
  xAxis: {
    type: 'category',
    data: store.thongKeItems.map(item => item.date),
    axisLabel: {
      formatter: function (value) {
        return formatDisplayDate(value);
      }
    }
  },
  yAxis: {
    type: 'value',
    name: t('amount')
  },
  series: [{
    data: store.thongKeItems.map(item => item.amount),
    type: 'line',
    smooth: true
  }]
}));

const serviceChartOption = computed(() => ({
  tooltip: {
    trigger: 'item',
    formatter: '{b}: {c} ' + t('timesUsed') + ' ({d}%)'
  },
  legend: {
    orient: 'vertical',
    left: 'left'
  },
  series: [
    {
      name: t('serviceUsage'),
      type: 'pie',
      radius: '50%',
      data: serviceRevenueData.value.map(item => ({
        value: item[1],
        name: item[0]
      })),
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }
  ]
}));

const { data: initialData, refresh } = await useAsyncData(
  'thongKeData',
  async () => {
    const today = new Date();
    const oneMonthAgo = new Date(today.getFullYear(), today.getMonth() - 1, today.getDate());
    const formatDate = (date: Date) => date.toISOString().split('T')[0];
    const startDate = formatDate(oneMonthAgo);
    const endDate = formatDate(today);
    dateRange.value = [startDate, endDate];
    doanhThuTheoDichVu.value = await store.getDoanhThuTheoDichVu(startDate, endDate);
    return await fetchDataInternal(startDate, endDate);
  }
);

const updateDateRange = (dates: [string, string]) => {
  dateRange.value = dates;
};

const formatDisplayDate = (dateString: string) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' });
};

const fetchDataInternal = async (startDate: string, endDate: string) => {
  const fetchFunction = {
    day: store.getUserThongKeTheoNgay,
    month: store.getUserThongKeTheoThang,
    year: store.getUserThongKeTheoNam
  }[statisticType.value];

  if (typeof fetchFunction !== 'function') {
    throw new Error('Invalid statistic type');
  }

  await fetchFunction(startDate, endDate);
  store.thongKeItems.sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime());

  // Thêm phần lấy doanh thu theo dịch vụ
  serviceRevenueData.value = await store.getDoanhThuTheoDichVu(startDate, endDate);

  return store.thongKeItems;
};

const fetchData = async () => {
  if (!dateRange.value[0] || !dateRange.value[1]) {
    toast.error(t('pleaseSelectDates'));
    return;
  }
  loading.value = true;
  error.value = null;
  try {
    const [startDate, endDate] = dateRange.value;
    await fetchDataInternal(startDate, endDate);
  } catch (err) {
    error.value = t('errorFetchingData');
    console.error('Error in fetchData:', err);
  } finally {
    loading.value = false;
  }
};

watch(statisticType, () => {
  if (dateRange.value[0] && dateRange.value[1]) {
    fetchData();
  }
});

onMounted(() => {
  refresh();
});
</script>

<style scoped>
.chart {
  height: 400px;
}

</style>