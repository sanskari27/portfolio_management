import FundService from '@/services/fund.service';
import { notFound } from 'next/navigation';
import { DataTable } from '../_components/data-table';

export default async function Page({
	searchParams,
}: {
	searchParams: {
		page: string;
		limit: string;
		search: string;
	};
}) {
	const data = await FundService.getInvestedFunds(searchParams);

	if (!data) {
		notFound();
	}

	return (
		<>
			<DataTable title={'Invested Funds'} records={data.data} maxRecord={data.pagination.total} />
		</>
	);
}
